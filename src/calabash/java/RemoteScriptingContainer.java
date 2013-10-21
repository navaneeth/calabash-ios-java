package calabash.java;

import static calabash.java.CalabashLogger.error;
import static calabash.java.CalabashLogger.info;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.jruby.embed.PathType;

import calabash.java.CalabashScriptExecutor.ExceptionResponse;
import calabash.java.CalabashScriptExecutor.GetLoadPathsResponse;
import calabash.java.CalabashScriptExecutor.Response;
import calabash.java.CalabashScriptExecutor.RunScriptletResponse;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class RemoteScriptingContainer implements IScriptingContainer {

	private final Server server;
	private final ConcurrentHashMap<String, Object> monitors = new ConcurrentHashMap<String, Object>();
	private final ConcurrentHashMap<String, Object> results = new ConcurrentHashMap<String, Object>();
	private final Object clientConnectedMonitor = new Object();

	public RemoteScriptingContainer() throws CalabashException {
		int port = 54555;
		String ip = "127.0.0.1";
		server = new Server(1048576, 1048576);
		Utils.registerClasses(server.getKryo());
		server.addListener(new ServerEventListener());
		server.start();
		try {
			server.bind(port);
		} catch (IOException e) {
			error("Failed to bind to port", e);
			throw new CalabashException("Failed to bind to port. "
					+ e.getMessage());
		}

		// Launch the client program which hosts the ScriptingContainer
		CommandLine cmdLine = new CommandLine(System.getProperty("java.home")
				+ "/bin/java");
		cmdLine.addArgument("-Dcalabash.remote.port=" + port);
		cmdLine.addArgument("-Dcalabash.remote.ip=" + ip);
		cmdLine.addArgument("-cp");
		cmdLine.addArgument(getClassPath());
		cmdLine.addArgument(CalabashScriptExecutor.class.getCanonicalName());
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		DefaultExecutor executor = new DefaultExecutor();
		try {
			executor.execute(cmdLine, resultHandler);
		} catch (Exception e) {
			error("Failed to launch remote script execution environment", e);
			throw new CalabashException(
					"Failed to launch remote script execution environment. "
							+ e.getMessage());
		}

		try {
			synchronized (clientConnectedMonitor) {
				clientConnectedMonitor.wait(5000);
			}
		} catch (InterruptedException e) {
			throw new CalabashException(
					"Failed to wait till client connected. Interrupted.", e);
		}
	}

	private String getClassPath() {
		try {
			URL[] urls = ((URLClassLoader) Thread.currentThread()
					.getContextClassLoader()).getURLs();
			String classPath = "";
			for (int i = 0; i < urls.length; i++) {
				classPath += urls[i].toString();
				if ((i + 1) != urls.length)
					classPath += java.io.File.pathSeparator;
			}
			return classPath;
		} catch (Exception e) {
			return System.getProperty("java.class.path");
		}
	}

	@Override
	public void put(String key, Object value) {
		PutRequest request = new PutRequest(key, value);
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
	}

	@Override
	public Object runScriptlet(String script) {
		RunScriptletRequest request = new RunScriptletRequest(script);
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
		return getResult(request.requestId);
	}

	@Override
	public void runScriptlet(PathType pathType, String filePath) {
		RunScriptletRequest request = new RunScriptletRequest(pathType,
				filePath);
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
		discardResult(request.requestId);
	}

	@Override
	public void clear() {
		ClearRequest request = new ClearRequest();
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
	}

	@Override
	public void terminate() {
		sendRequest(new TerminateRequest());
		server.stop();
		server.close();
	}

	@Override
	public void setHomeDirectory(String absolutePath) {
		SetHomeDirectoryRequest request = new SetHomeDirectoryRequest(absolutePath);
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
	}

	@Override
	public void setEnvironment(HashMap<String, String> environmentVariables) {
		SetEnvironmentVariablesRequest request = new SetEnvironmentVariablesRequest(environmentVariables);
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getLoadPaths() {
		GetLoadPathRequest request = new GetLoadPathRequest();
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
		try {
			List<String> result = (List<String>) getResult(request.requestId);
			return new RemoteList(result);
		} catch (Throwable e) {
			return new RemoteList();
		}
	}
	
	@Override
	public void setLogsDirectory(File logFile) {
		EnableLoggingRequest request = new EnableLoggingRequest(logFile.getAbsolutePath());
		Object monitor = new Object();
		monitors.put(request.requestId, monitor);
		sendRequest(request);
		waitForResults(monitor);
	}

	@Override
	public void setErrorWriter(Writer writer) {
	}

	private void sendRequest(Request request) {
		getConnection().sendTCP(request);
	}

	private Connection getConnection() {
		if (server.getConnections().length > 0)
			return server.getConnections()[0];

		throw new RuntimeException("No client connected");
	}

	private Object getResult(String requestId) {
		if (results.containsKey(requestId)) {
			Object object = results.get(requestId);
			results.remove(requestId);
			if (object instanceof Throwable) {
				Throwable error = (Throwable) object;
				throw new RuntimeException(error);
			}
			return object;
		}

		return null;
	}

	private void discardResult(String requestId) {
		if (results.containsKey(requestId)) {
			results.remove(requestId);
		}
	}

	private Object popMonitor(String requestId) {
		Object monitor = monitors.get(requestId);
		if (monitor != null) {
			monitors.remove(requestId);
			return monitor;
		}

		return null;
	}

	private void waitForResults(Object monitor) {
		synchronized (monitor) {
			try {
				monitor.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	class ServerEventListener extends Listener {
		@Override
		public void received(Connection connection, Object message) {
			info("Server: " + message);
			if (message instanceof String) {
				String m = (String) message;
				if ("ping".equals(m))
					connection.sendTCP("pong");
			} else if (message instanceof RunScriptletResponse) {
				RunScriptletResponse r = (RunScriptletResponse) message;
				Object monitor = popMonitor(r.requestId);
				if (r.data != null)
					results.put(r.requestId, r.data);
				if (monitor != null) {
					synchronized (monitor) {
						monitor.notify();
					}
				}
			} else if (message instanceof ExceptionResponse) {
				ExceptionResponse r = (ExceptionResponse) message;
				Object monitor = popMonitor(r.requestId);
				results.put(r.requestId, new Throwable(r.error));
				synchronized (monitor) {
					monitor.notify();
				}
			} else if (message instanceof GetLoadPathsResponse) {
				GetLoadPathsResponse r = (GetLoadPathsResponse) message;
				Object monitor = popMonitor(r.requestId);
				results.put(r.requestId, r.loadPaths);
				synchronized (monitor) {
					monitor.notify();
				}
			} else if (message instanceof Response) {
				Response r = (Response) message;
				Object monitor = popMonitor(r.requestId);
				if (monitor != null) {
					synchronized (monitor) {
						monitor.notify();
					}
				}
			}
		}

		@Override
		public void connected(Connection arg0) {
			synchronized (clientConnectedMonitor) {
				clientConnectedMonitor.notify();
			}
		}
	}

	class RemoteList extends ArrayList<String> {
		private static final long serialVersionUID = -8479645165780638416L;

		public RemoteList() {
		}

		public RemoteList(Collection<String> c) {
			super(c);
		}

		@Override
		public boolean addAll(Collection<? extends String> c) {
			sendRequest(new AddLoadPathRequest(c));
			return true;
		}
	}

	static abstract class Request implements Serializable {
		private static final long serialVersionUID = 867883264433337973L;
		public String requestId;

		public Request() {
			this.requestId = UUID.randomUUID().toString();
		}
	}

	static class RunScriptletRequest extends Request {
		private static final long serialVersionUID = -5771451238821585307L;
		public String script;
		public String fileName;
		public PathType pathType;

		public RunScriptletRequest() {
		}

		public RunScriptletRequest(String script) {
			this.script = script;
		}

		public RunScriptletRequest(PathType pathType, String fileName) {
			this.pathType = pathType;
			this.fileName = fileName;
		}
	}

	static class AddLoadPathRequest extends Request {
		private static final long serialVersionUID = -8572541727990523058L;
		public List<String> loadPath;

		public AddLoadPathRequest() {
		}

		public AddLoadPathRequest(Collection<? extends String> c) {
			this.loadPath = new ArrayList<String>(c);
		}
	}

	static class ClearRequest extends Request {
		private static final long serialVersionUID = 798797505174962279L;
	}

	static class PutRequest extends Request {
		private static final long serialVersionUID = 3337038131308201283L;
		public String key;
		public Object value;

		public PutRequest() {
		}

		public PutRequest(String key, Object value) {
			this.key = key;
			this.value = value;
		}
	}

	static class SetHomeDirectoryRequest extends Request {
		private static final long serialVersionUID = -3877240401760381576L;
		public String homeDirectory;

		public SetHomeDirectoryRequest() {
		}

		public SetHomeDirectoryRequest(String homeDirectory) {
			this.homeDirectory = homeDirectory;
		}
	}

	static class TerminateRequest extends Request {
		private static final long serialVersionUID = 2056011144800940447L;
	}

	static class SetEnvironmentVariablesRequest extends Request {
		public HashMap<String, String> environmentVariables;

		public SetEnvironmentVariablesRequest() {
		}

		public SetEnvironmentVariablesRequest(
				HashMap<String, String> environmentVariables) {
			this.environmentVariables = environmentVariables;
		}

		private static final long serialVersionUID = -8587548120543298977L;
	}

	static class GetLoadPathRequest extends Request {
		private static final long serialVersionUID = 6351574977055274197L;
		public List<String> loadPath;
	}
	
	static class EnableLoggingRequest extends Request {
		private static final long serialVersionUID = -1701602165823249504L;
		public String logFile;
		
		public EnableLoggingRequest() {
		}
		
		public EnableLoggingRequest(String logFile) {
			this.logFile = logFile;
		}
	}
}

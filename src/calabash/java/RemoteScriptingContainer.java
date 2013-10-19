package calabash.java;

import static calabash.java.CalabashLogger.error;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.jruby.embed.PathType;


import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class RemoteScriptingContainer implements IScriptingContainer {

	private final Server server;
	private final ConcurrentHashMap<String, Object> monitors = new ConcurrentHashMap<String, Object>();
	private final ConcurrentHashMap<String, Object> results = new ConcurrentHashMap<String, Object>();

	public RemoteScriptingContainer() throws CalabashException {
		int port = 54555;
		String ip = "127.0.0.1";
		server = new Server();
		Utils.registerClasses(server.getKryo());

		server.start();
		try {
			server.bind(port);
		} catch (IOException e) {
			error("Failed to bind to port", e);
			throw new CalabashException("Failed to bind to port. "
					+ e.getMessage());
		}

		// Launch the client program which hosts the ScriptingContainer
		CommandLine cmdLine = new CommandLine("java");
		cmdLine.addArgument("-Dcalabash.remote.port=" + port);
		cmdLine.addArgument("-Dcalabash.remote.ip=" + ip);
		cmdLine.addArgument("-cp");
		cmdLine.addArgument(System.getProperty("java.class.path"));
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
	}

	@Override
	public void put(String key, Object value) {
		sendRequest(new PutRequest(key, value));
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
		sendRequest(new ClearRequest());
	}

	@Override
	public void terminate() {
		sendRequest(new TerminateRequest());
	}

	@Override
	public void setHomeDirectory(String absolutePath) {
		sendRequest(new SetHomeDirectoryRequest(absolutePath));
	}

	@Override
	public void setEnvironment(HashMap<String, String> environmentVariables) {
		sendRequest(new SetEnvironmentVariablesRequest(environmentVariables));
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
			return (List<String>) getResult(request.requestId);
		} catch (Throwable e) {
			return new RemoteList();
		}
	}

	@Override
	public void setErrorWriter(Writer writer) {
	}

	private void sendRequest(Request request) {
		server.getConnections()[0].sendTCP(request);
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
			System.out.println(message);
			if (message instanceof String) {
				String m = (String) message;
				if ("ping".equals(m))
					connection.sendTCP("pong");
			} else if (message instanceof RunScriptletResponse) {
				RunScriptletResponse r = (RunScriptletResponse) message;
				Object monitor = popMonitor(r.requestId);
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
	}

	class RemoteList extends ArrayList<String> {
		private static final long serialVersionUID = -8479645165780638416L;

		@Override
		public boolean addAll(Collection<? extends String> c) {
			sendRequest(new AddLoadPathRequest(c));
			return true;
		}
	}

}

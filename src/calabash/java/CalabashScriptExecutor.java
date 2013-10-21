package calabash.java;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import calabash.java.RemoteScriptingContainer.AddLoadPathRequest;
import calabash.java.RemoteScriptingContainer.ClearRequest;
import calabash.java.RemoteScriptingContainer.EnableLoggingRequest;
import calabash.java.RemoteScriptingContainer.GetLoadPathRequest;
import calabash.java.RemoteScriptingContainer.PutRequest;
import calabash.java.RemoteScriptingContainer.RunScriptletRequest;
import calabash.java.RemoteScriptingContainer.SetEnvironmentVariablesRequest;
import calabash.java.RemoteScriptingContainer.SetHomeDirectoryRequest;
import calabash.java.RemoteScriptingContainer.TerminateRequest;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import static calabash.java.CalabashLogger.info;

public class CalabashScriptExecutor {

	private final Client client = new Client(1048576, 1048576);
	private final static Object monitor = new Object();

	public static void main(String[] args) throws Exception {
		new CalabashScriptExecutor();
		synchronized (monitor) {
			monitor.wait();
		}
	}

	private final ScriptingContainer container = new ScriptingContainer(
			LocalContextScope.SINGLETHREAD);

	public CalabashScriptExecutor() throws NumberFormatException, IOException {
		Utils.registerClasses(client.getKryo());
		client.addListener(new RequestListener());
		client.start();
		client.connect(5000, System.getProperty("calabash.remote.ip"),
				Integer.parseInt(System.getProperty("calabash.remote.port")));
	}

	class RequestListener extends Listener {
		@Override
		public void received(Connection connection, Object object) {
			info("Client: " + object);
			if (object instanceof EnableLoggingRequest) {
				EnableLoggingRequest r = (EnableLoggingRequest) object;
				CalabashConfiguration config = new CalabashConfiguration();
				try {
					config.setLogsDirectory(new File(r.logFile));
					CalabashLogger.initialize(config);
					client.sendTCP(new Response(r.requestId));
				} catch (CalabashException e) {
					client.sendTCP(new ExceptionResponse(r.requestId, e));
				}
			} else if (object instanceof ClearRequest) {
				ClearRequest r = (ClearRequest) object;
				container.clear();
				client.sendTCP(new Response(r.requestId));
			} else if (object instanceof RunScriptletRequest) {
				RunScriptletRequest r = (RunScriptletRequest) object;
				try {
					Object result = null;
					if (r.fileName != null && r.pathType != null) {
						container.runScriptlet(r.pathType, r.fileName);
					} else {
						Object runScriptlet = container.runScriptlet(r.script);
						result = Utils.toJavaObject(runScriptlet);
					}

					client.sendTCP(new RunScriptletResponse(r.requestId, result));
				} catch (Exception e) {
					client.sendTCP(new ExceptionResponse(r.requestId, e));
				}
			} else if (object instanceof SetEnvironmentVariablesRequest) {
				SetEnvironmentVariablesRequest r = (SetEnvironmentVariablesRequest) object;
				container.setEnvironment(r.environmentVariables);
				client.sendTCP(new Response(r.requestId));
			} else if (object instanceof SetHomeDirectoryRequest) {
				SetHomeDirectoryRequest r = (SetHomeDirectoryRequest) object;
				container.setHomeDirectory(r.homeDirectory);
				client.sendTCP(new Response(r.requestId));
			} else if (object instanceof TerminateRequest) {
				container.terminate();
				System.exit(0);
			} else if (object instanceof PutRequest) {
				PutRequest r = (PutRequest) object;
				container.put(r.key, r.value);
				client.sendTCP(new Response(r.requestId));
			} else if (object instanceof GetLoadPathRequest) {
				GetLoadPathRequest r = (GetLoadPathRequest) object;
				List<String> loadPaths = container.getLoadPaths();
				client.sendTCP(new GetLoadPathsResponse(r.requestId, loadPaths));
			} else if (object instanceof AddLoadPathRequest) {
				AddLoadPathRequest r = (AddLoadPathRequest) object;
				container.getLoadPaths().addAll(r.loadPath);
				client.sendTCP(new Response(r.requestId));
			}
		}

		@Override
		public void disconnected(Connection arg0) {
			System.exit(0);
		}
	}

	static class GetLoadPathsResponse extends Response {
		private static final long serialVersionUID = -7001653582480568128L;
		public List<String> loadPaths;

		public GetLoadPathsResponse() {
		}

		public GetLoadPathsResponse(String requestId, List<String> loadPaths) {
			super(requestId);
			this.loadPaths = loadPaths;
		}
	}

	static class Response implements Serializable {
		private static final long serialVersionUID = 1654428568551188101L;
		public String responseId;
		public String requestId;

		public Response() {
		}

		public Response(String requestId) {
			this.requestId = requestId;
			responseId = UUID.randomUUID().toString();
		}
	}

	static class RunScriptletResponse extends Response {
		private static final long serialVersionUID = -1340817688151164183L;
		public Object data;

		public RunScriptletResponse() {
		}

		public RunScriptletResponse(String requestId, Object data) {
			super(requestId);
			this.data = data;
		}
	}

	static class ExceptionResponse extends Response {
		private static final long serialVersionUID = 8949922243139635015L;
		public String error;

		public ExceptionResponse() {
		}

		public ExceptionResponse(String requestId, Throwable error) {
			super(requestId);
			this.error = error.getMessage();
		}
	}
}

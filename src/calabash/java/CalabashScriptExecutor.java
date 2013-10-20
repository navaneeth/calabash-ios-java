package calabash.java;

import java.io.IOException;
import java.util.List;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class CalabashScriptExecutor {

	private final Client client = new Client();
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
			System.out.println("Client got - " + object);
			if (object instanceof ClearRequest) {
				container.clear();
			} else if (object instanceof RunScriptletRequest) {
				RunScriptletRequest r = (RunScriptletRequest) object;
				try {
					Object result = container.runScriptlet(r.script);
					client.sendTCP(new RunScriptletResponse(r.requestId, result));
				} catch (Exception e) {
					client.sendTCP(new ExceptionResponse(r.requestId, e));
				}
			} else if (object instanceof SetEnvironmentVariablesRequest) {
				SetEnvironmentVariablesRequest r = (SetEnvironmentVariablesRequest) object;
				container.setEnvironment(r.environmentVariables);
			} else if (object instanceof SetHomeDirectoryRequest) {
				SetHomeDirectoryRequest r = (SetHomeDirectoryRequest) object;
				container.setHomeDirectory(r.homeDirectory);
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
			}
		}

		@Override
		public void disconnected(Connection arg0) {
			System.exit(0);
		}
	}
}

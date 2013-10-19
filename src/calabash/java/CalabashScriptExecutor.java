package calabash.java;

import java.io.IOException;
import java.util.List;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;


import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class CalabashScriptExecutor {

	private Client client;
	private boolean pingSuccess = false;
	private final Object locker = new Object();
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
		client = new Client();
		Utils.registerClasses(client.getKryo());
		client.addListener(new RequestListener());
		client.start();
		client.connect(5000, System.getProperty("calabash.remote.ip"),
				Integer.parseInt(System.getProperty("calabash.remote.port")));
		Thread watchDog = new Thread(new ServerWatchDog());
		//watchDog.start();
	}

	private void markPingStatus(boolean status) {
		synchronized (locker) {
			pingSuccess = status;
		}
	}

	class RequestListener extends Listener {
		@Override
		public void received(Connection connection, Object object) {
			System.out.println(object);
			if (object instanceof String) {
				String m = (String) object;
				if ("pong".equals(m))
					markPingStatus(true);
			} else if (object instanceof ClearRequest) {
				container.clear();
			} else if (object instanceof RunScriptletRequest) {
				RunScriptletRequest r = (RunScriptletRequest) object;
				try {
					Object result = container.runScriptlet(r.script);
					if (result != null) {
						Object javaObject = Utils.toJavaObject(result);
						client.sendTCP(new RunScriptletResponse(r.requestId,
								javaObject));
					}
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
			}
			else if (object instanceof GetLoadPathRequest) {
				GetLoadPathRequest r = (GetLoadPathRequest) object;
				List<String> loadPaths = container.getLoadPaths();
				client.sendTCP(new GetLoadPathsResponse(r.requestId, loadPaths));
			}
			else if (object instanceof AddLoadPathRequest) {
				AddLoadPathRequest r = (AddLoadPathRequest) object;
				container.getLoadPaths().addAll(r.loadPath);
			}
		}
	}

	class ServerWatchDog implements Runnable {

		@Override
		public void run() {
			while (true) {
				markPingStatus(false);
				client.sendTCP("ping");
				Utils.sleep(2000);
//				if (!pingSuccess)
//					System.exit(0); // Server died
			}
		}

	}

}

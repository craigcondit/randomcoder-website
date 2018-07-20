package org.randomcoder.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

@SuppressWarnings("javadoc")
abstract public class TestHttpServer {
	protected final ServerSocket socket;

	public TestHttpServer() throws IOException {
		socket = new ServerSocket();
		socket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), 0));

		new Thread() {
			@Override
			public void run() {
				try {
					while (true)
						accept(socket.accept());
				} catch (SocketException e) {
					// shutdown, don't log
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	abstract protected void process(Socket connection, String verb, String uri, Map<String, String> headers)
			throws IOException;

	public int getPort() {
		return socket.getLocalPort();
	}

	public void destroy() throws IOException {
		socket.close();
	}

	protected void sendResponse(Socket connection, String status, Map<String, String> headers, InputStream data)
			throws IOException {
		OutputStream out = null;
		PrintWriter writer = null;

		try {
			out = connection.getOutputStream();
			writer = new PrintWriter(out);
			writer.print("HTTP/1.0 " + status + "\r\n");

			// write headers
			for (String header : headers.keySet()) {
				writer.print(header + ": " + headers.get(header) + "\r\n");
			}
			writer.print("\r\n");
			writer.flush();
			if (data != null) {
				byte[] buf = new byte[1024];
				int c;
				do {
					c = data.read(buf, 0, buf.length);
					if (c > 0)
						out.write(buf, 0, c);
					out.flush();
				} while (c >= 0);
				writer.close();
			}
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (Exception ignored) {
				}
			if (out != null)
				try {
					out.close();
				} catch (Exception ignored) {
				}
		}
	}

	protected void sendError(Socket connection, String status)
			throws IOException {
		Map<String, String> headers = getDefaultHeaders();
		headers.put("Content-type", "text/plain");
		sendResponse(connection, status, headers, new ByteArrayInputStream(status.getBytes("UTF-8")));
	}

	protected Map<String, String> getDefaultHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Date", formatDateHeader(new Date()));
		return headers;
	}

	protected String formatDateHeader(Date value) {
		SimpleDateFormat sdf = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(value);
	}

	protected void accept(Socket connection) throws IOException {
		InputStream is = connection.getInputStream();
		if (is == null)
			return;
		BufferedReader in = new BufferedReader(new InputStreamReader(is));

		StringTokenizer st = new StringTokenizer(in.readLine());
		if (!st.hasMoreTokens()) {
			sendError(connection, "400 Bad Request");
			return;
		}

		String method = st.nextToken().toLowerCase(Locale.US);

		if (!st.hasMoreTokens()) {
			sendError(connection, "400 Bad Request");
			return;
		}

		String uri = URLDecoder.decode(st.nextToken(), "UTF-8");

		Map<String, String> headers = new HashMap<String, String>();

		// read headers
		String line = in.readLine();
		while (line.trim().length() > 0) {
			int eqLoc = line.indexOf(':');
			headers.put(line.substring(0, eqLoc).trim().toLowerCase(Locale.US), line.substring(eqLoc + 1).trim());
			line = in.readLine();
		}

		process(connection, method, uri, headers);

		in.close();
		connection.close();
	}
}

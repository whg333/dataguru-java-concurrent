package cn.dataguru.week8;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class HomeWorkClientGUI extends JFrame {

	private static final long serialVersionUID = -3410510899537209998L;

	public static void main(String[] args) {
		String server = args.length > 0 ? args[0] : "localhost";
		int servPort = args.length > 1 ? Integer.parseInt(args[1]) : 8091;

		JFrame frame = new HomeWorkClientGUI(server, servPort);
		frame.setVisible(true);
	}

	public HomeWorkClientGUI(String server, int servPort) {
		super("week8 HomeWork Client");
		setSize(300, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JTextField send = new JTextField();
		getContentPane().add(send, "South");

		final JTextArea reply = new JTextArea(8, 20);
		reply.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(reply);
		getContentPane().add(scrollPane, "Center");

		final Socket socket;
		final DataInputStream in;
		final OutputStream out;
		try {
			socket = new Socket(server, servPort);

			in = new DataInputStream(socket.getInputStream());
			out = socket.getOutputStream();
			send.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (event.getSource() == send) {
						String writeStr = send.getText();
						if("quit".equals(writeStr)){
							close(socket);
						}
						byte[] byteBuffer = (writeStr + "\r\n").getBytes();
						byte[] readBuffer = new byte[256];
						try {
							out.write(byteBuffer);
							int readSize;
							if ((readSize = in.read(readBuffer)) > 0) {
								reply.append(writeStr + "\n" + new String(Arrays.copyOfRange(readBuffer, 0, readSize)));
							}
							send.setText("");
						} catch (IOException e) {
							e.printStackTrace();
							reply.append("ERROR\n");
						}
					}
				}
			});

			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					close(socket);
				}
			});
		} catch (IOException exception) {
			exception.printStackTrace();
			reply.append(exception.toString() + "\n");
		}
		
	}
	
	private void close(Socket socket){
		try {
			socket.close();
		} catch (Exception exception) {
		}
		System.exit(0);
	}
}

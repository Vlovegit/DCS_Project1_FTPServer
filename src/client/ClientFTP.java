package client;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientFTP {
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;

	public static void main(String[] args)
	{
		// Create Client Socket connect to port 900
		try (Socket socket = new Socket("localhost", 900)) 
		{
			String usrInput = ""; 
			System.out.println("Welcome to our DCS Project 1\n");
			Scanner sc = new Scanner(System.in);
			String currentDir = System.getProperty("user.dir");
			while(!(usrInput.equals("quit")))
			{
				System.out.println("\nList of available commands\n");
				System.out.println("1. get - Fetch remote file from remote server ( get <remote_filename> )\n");
				System.out.println("2. put - Send local file to remote server ( put <local_filename> )\n");
				System.out.println("3. delete - Delete file from remote server ( delete <remote_filename> )\n");
				System.out.println("4. ls - List all files and subdirectories in the remote server ( ls )\n");
				System.out.println("5. cd - Change Directory to mentioned path or parent directory in the remote server ( cd <remote_directory_name> or cd .. )\n");
				System.out.println("6. mkdir - Create a subdirectory in current working directory in the remote server ( mkdir <remote_directory_name> )\n");
				System.out.println("7. pwd - Fetch present working directory in the remote server ( pwd )\n");
				System.out.println("8. quit - Close the connection with the remote server ( quit )\n\n");

				System.out.println("Enter your command to proceed : \n");
				usrInput = sc.nextLine();
				String splitCommand[] = usrInput.split(" ");
				dataInputStream = new DataInputStream(socket.getInputStream());
				dataOutputStream = new DataOutputStream(socket.getOutputStream());
				switch(splitCommand[0])
				{
					case  "put": 	dataOutputStream.writeUTF(splitCommand[0]);
									System.out.println("Sending the File to the Server\n");
									//System.out.println(currentDir+"Client/Files/".concat(splitCommand[1]));
									sendFile(currentDir+"/src/client/files/".concat(splitCommand[1]));
									break;
					case "quit":	dataOutputStream.writeUTF(splitCommand[0]);
									dataInputStream.close();
									dataOutputStream.close();
									break;
					default : 		System.out.println("Please enter a valid command");
									break;
				}
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendFile(String path) throws Exception
	{
		int bytes = 0;
		File file = new File(path);
		FileInputStream fileInputStream = new FileInputStream(file);

		// Here we send the File to Server
		dataOutputStream.writeLong(file.length());
		// Here we break file into chunks
		byte[] buffer = new byte[4 * 1024];
		while ((bytes = fileInputStream.read(buffer))
			!= -1) {
		// Send the file to Server Socket
		dataOutputStream.write(buffer, 0, bytes);
			dataOutputStream.flush();
		}
		// close the file here
		fileInputStream.close();
	}
}


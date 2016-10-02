package com.mkyong.rest;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

@Path("/file")
public class UploadFileService {

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadFile(
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {

		String uploadedFileLocation = "/Users/sandsaha/uploaded/"
				+ fileDetail.getFileName();

		// save it
		writeToFile(uploadedInputStream, uploadedFileLocation);

		String panNumber = getApproxPanNumber(uploadedFileLocation);

		String output = "PAN number is : " + panNumber;

		return Response.status(200).entity(output).build();

	}

	private String getApproxPanNumber(String fileLocation){
		Socket clientSocket = null;
		OutputStream outputStream = null;
		DataOutputStream dataOutputStream = null;
		InputStream inputStream = null;
		String panNumber = null;

		try {
			clientSocket = new Socket("10.68.52.126", 6002);
			outputStream = clientSocket.getOutputStream();

			dataOutputStream = new DataOutputStream(outputStream);
			dataOutputStream.writeUTF("Hello");
			System.out.println("Client to server says: Hello");
			BufferedImage image = ImageIO.read(new File(fileLocation));
			ImageIO.write(image, "PNG", clientSocket.getOutputStream());
			System.out.println("Client: Image sent to server");

			//Thread.currentThread().wait(10000);
			inputStream = clientSocket.getInputStream();
			DataInputStream dis = new DataInputStream(inputStream);

			panNumber = dis.readUTF();
			System.out.println("PAN Code is:" + panNumber);

			dataOutputStream.close();
			outputStream.close();
			inputStream.close();
			clientSocket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return panNumber;
	}
	// save uploaded file to new location
	private void writeToFile(InputStream uploadedInputStream,
			String uploadedFileLocation) {

		try {
			OutputStream out = new FileOutputStream(new File(
					uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

}
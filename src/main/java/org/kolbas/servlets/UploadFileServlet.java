package org.kolbas.servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.kolbas.files.FileLoader;
import org.kolbas.files.FileSaver;
import org.kolbas.storage.MapStorage;
import org.kolbas.storage.ResponseSaver;
import org.kolbas.servlets.PublicCollectionSingleton;


/**
 * Servlet implementation class UploadFileServlet
 * Локализацию не делал. Но сделал бы через синглтон.
 */

@MultipartConfig
public class UploadFileServlet extends HttpServlet {

	private final int MAX_FILE_SIZE = 10000;
	private final String ERROR_FILE = "ERROR";
	private final String ERROR_STRING = "ERROR";
	private final String ERROR_DIR = "ERROR";
	
	/**
	 *  Создает директорию для загрузки файлов.
	 */
	private String createUploadDir(String path, String dirName) {
		path = path + "\\" + dirName;
		File dir = new File(path);
		dir.mkdirs();
		return path;	
	}

	/**
	 *  Насколько я понял, если папка у нас общая - нужно обеспечить уникальность имен файлов,
	 *	загруженными разными пользователями. Пока заглушка.
	 */
	private String getFileName(Part part) {
		return part.getSubmittedFileName();
	}

	/**
	 *  Сохраняет файл из post-запроса в директорию dirName.
	 */
	private String saveToDir(Part part, String dirName) {
		String fname = ERROR_FILE;
		FileLoader loader = null;
		FileSaver saver = null;
		try {
			loader = new FileLoader(part.getInputStream());
			fname = dirName + "\\" +getFileName(part);

			saver = new FileSaver(fname);

			while (true) {
				String str = loader.next();
				if (str == null)
					break;
				saver.write(str);
				
			}
			saver.close();
			loader.close();
		} catch (IOException ex) {
			fname = ERROR_FILE;
		}
		return fname;
	}

	/**
	 *  Получает String-параметр из post-запроса. 
	 */
	private String saveToString(Part part) {
		FileLoader loader;
		String buf = "";
		try {
			loader = new FileLoader(part.getInputStream());
			while (true) {
				String str = loader.next();
				if (str == null)
					break;
				else
					buf += str;
			}
			loader.close();
		} catch (IOException e) {
			buf = ERROR_STRING;
		}
		return buf;
	}

	
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");

		System.out.println(request.getCharacterEncoding());
		PrintWriter out;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        //не понял как с помощью web-xml создать директорию для загрузки файлов.
		//создал вручную
		String uploadDirPath = createUploadDir(
				getServletContext().getRealPath(""), "upload");

		int listCapasity = 10;
		List<String> listFiles = new ArrayList<String>(listCapasity);

		String listValue = "";
		Class<?> plugin = null;

		try {
			for (Part part : request.getParts()) {
				if (part.getSubmittedFileName() != null) {

					if (part.getSize() < MAX_FILE_SIZE) {

						String fname = saveToDir(part, uploadDirPath);
						if (fname != ERROR_FILE) {
							listFiles.add(fname);
							out.println("Файл '" + part.getSubmittedFileName()
									+ "' был успешно загружен.<br>");
						} else {
							out.println("При загрузке файла '"
									+ part.getSubmittedFileName()
									+ "' произошла ошибка.");
						}
					} else {
						out.println("Файл '"
								+ part.getSubmittedFileName()
								+ "' не был загружен, так как имеет слишком большой размер.<br>");
					}
				} else {
					listValue = saveToString(part);
					if (listValue == ERROR_STRING) {
						out.println("Не удалось сохранить параметр '"
								+ part.getName() + "'.");
						return;
					} else {
						plugin = PublicCollectionSingleton.getInstance().getPlugin(listValue);
					}
				}
			}
		} catch (ServletException | IOException e) {
			e.printStackTrace();
			return;
		}

		boolean hasError = false;
		if (plugin == null) {
			out.println("Не найден плагин '" + listValue + "'.");
			hasError = true;
		}

		if (listFiles.size() == 0) {
			out.println("Ни один файл не был загружен успешно.");
			hasError = true;
		}

		if (hasError)
			return;

		String[] masStream = listFiles.toArray(new String[] {});
		ResponseSaver saver = new ResponseSaver(masStream, out,
				new MapStorage(), plugin);
		saver.saveToResponse(out);
	}
}

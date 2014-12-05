package org.kolbas.storage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.kolbas.threads.FileReaderThreadPool;
import org.kolbas.threads.QueueReaderThreadPool;


/**
 * Попытался максимально использовать, имеющийся код из задания с многопоточностью.
 * Сохраняет удаленные из файлов слова в response сервлета.
 * 
 * 
 */
public class ResponseSaver implements Storageable {

	private BlockingQueue<String> queue;
	private Storageable storage;
	private FileReaderThreadPool poolFileReaders;
	private QueueReaderThreadPool poolQueueReaders;

	public ResponseSaver(String[] files, PrintWriter out, Storageable storage,
			Class<?> plugin) {

		this.storage = storage;
		
		int queueCapasity = 10000;
		queue = new LinkedBlockingQueue<String>(queueCapasity);


		int countFileReaderThread = files.length;

		int startArgs = 0;
		poolFileReaders = new FileReaderThreadPool(files, startArgs,
				countFileReaderThread, queue);

		int countQueueReaderThread = Runtime.getRuntime().availableProcessors();
		poolQueueReaders = new QueueReaderThreadPool(countQueueReaderThread,
				queue, storage, poolFileReaders, plugin);
		
		
		poolFileReaders.start();
		poolQueueReaders.start();

		try {
			poolFileReaders.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			poolQueueReaders.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Boolean contains(String arg0) {
		// TODO Auto-generated method stub
		return storage.contains(arg0);
	}

	@Override
	public Boolean put(String arg0) {
		// TODO Auto-generated method stub
		return storage.put(arg0);
	}

	@Override
	public void saveToFile(String arg0) throws IOException {
		// TODO Auto-generated method stub
			storage.saveToFile(arg0);
	}

	@Override
	public void saveToResponse(PrintWriter out) {
		out.println("Список удаленных слов:<br>");
		storage.saveToResponse(out);
	}

}

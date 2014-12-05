package org.kolbas.servlets;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class PublicCollectionSingleton {

	
	private ConcurrentMap<String, String> map;

	private static volatile PublicCollectionSingleton instance;

	
	/**
	 * Паттерн Синглтон, нужен для того, чтобы держать только одну копию модулей для всех запросов.
	 * @return
	 */
	public static PublicCollectionSingleton getInstance() {
		PublicCollectionSingleton localInstance = instance;
		if (localInstance == null) {
			synchronized (PublicCollectionSingleton.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new PublicCollectionSingleton();
				}
			}
		}
		return localInstance;
	}

	/**
	 * Инициализирует переменные. Пока доступен только один плагин.
	 * @return
	 */
	private PublicCollectionSingleton() {
		map = new ConcurrentHashMap<String, String>();
		map.put("Первый модуль", "org.kolbas.modules.first.FirstModule");
	}

	/**
	 * 
	 * @return true, если модуль существует.
	 */
	public boolean contains(String key) {

		return map.containsKey(key);
	}

	/**
	 * 
	 * @return null, если плагин key не найден.
	 */
	public Class<?> getPlugin(String key) {

		try {
			if (map.containsKey(key))
				return Class.forName(map.get(key));
			else
				return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	/**
	 * генерирует html добавку к <input type="list" name =listName> для выбора из доступных плагинов.
	 * @param listName - имя input'а, к которому формируется добавка. 
	 * Наверно, код inputa нужен сюда же.
	 * @return null, если плагин key не найден.
	 */
	public String toHtmlDataList(String listName) {
		String res = "* <input list='"+listName+"' name='list' required /> ";
		res += " <datalist id='" + listName + "'> ";

		for (String key : map.keySet()) {
			res += " <option value='" + key + "'> ";
		}
		res += "</datalist>";
		return res;
	}

}
<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ page import="org.kolbas.servlets.PublicCollectionSingleton;" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Отправка файла на сервер</title>

<title>Insert title here</title>
</head>
<body>
	<p>
		Разработать веб-приложение (на основе сервлетов или JSP), которое
		будет реализовывать следующее:
		<ul>
			<li>загрузку файлов для анализа;</li>
			<li>выбор модуля анализа из прошлого задания;</li>
			<li>производить анализ файлов и выводить результат пользователю.</li>
		</ul>
	</p>
	<form enctype="multipart/form-data" method="post" action="Result.xhtml">
		<p>
			* <input title="Выберите txt-файл(ы)" required multiple type="file"
				name="txtFile" accept="text/plain"/>
		</p>
		<p>
		<%= PublicCollectionSingleton.getInstance().toHtmlDataList("listJar")   
		 %>
		</p>
		<p>
			<input type="submit" value="Отправить" />
		</p>
	</form>
</body>
</html>
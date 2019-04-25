<!-- DOCTYPE is an instruction to the browser about what version of HTML
the page is written in -->
<!DOCTYPE html>
<html>

  <!-- Head contains meta data and imports -->
  <head>
	<link rel="stylesheet" href="/css/normalize.css">
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/html5bp.css">
    <script src="/js/jquery-3.1.1.js"></script>

    <!-- define character set in use -->
    <meta charset="utf-8">

    <!-- Title appears in tab -->
    <title>${title}</title>

  </head>

  <!-- Body contains the page content -->
  <body>
    <div id="page-body">${content}</div>

    <script src="/js/jquery-3.1.1.js"></script>
    <script src="/js/session.js"></script>
    <script src="/js/dashboard.js"></script>
    <script src="/js/landing.js"></script>
    <script src="/js/group.js"></script>
    <script src="/js/newgroup.js"></script>
    <script src="https://apis.google.com/js/api:client.js"></script>

  </body>

<!-- Make sure to close all your tags! -->
</html>

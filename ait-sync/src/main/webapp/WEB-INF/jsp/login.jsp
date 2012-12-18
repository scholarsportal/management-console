<html>
<body>      
<form
        id="loginForm"
        action="${pageContext.request.contextPath}/j_spring_security_check"
        method="POST">
        <fieldset>
          <ol>
            <li><label for="username">Username</label> <input
              name="j_username"
              type="text"
              autofocus="true"
              id="username" /></li>

            <li><label>Password</label> <input
              name="j_password"
              type="password"
              id="password" /></li>
          </ol>
        </fieldset>

        <fieldset>
          <button
            class="primary"
            type="submit"
            id="login-button">Login</button>
        </fieldset>
      </form>
</body>
</html>



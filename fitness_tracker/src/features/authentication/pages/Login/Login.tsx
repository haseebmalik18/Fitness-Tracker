import { FormEvent, useState } from "react";
import classes from "./Login.module.scss";
import { Box } from "../../components/Box/Box";
import { Input } from "../../components/Input/Input";
import { Button } from "../../components/Button/Button";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { Seperator } from "../../components/Seperator/Seperator";
import { useAuthentication } from "../../contexts/AuthenticationContextProvider";
import { Layout } from "../../components/AuthenticationLayout/Layout";

export function Login() {
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuthentication();
  const navigate = useNavigate();
  const location = useLocation();

  const doLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;

    try {
      await login(email, password);
      const destination = location.state?.from || "/";
      navigate(destination);
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessage(error.message);
        const apiUrl = import.meta.env.VITE_API_URL;
        console.log(apiUrl); // Should log: http://localhost:8080
      } else {
        setErrorMessage("An unknown error occurred. Please try again.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Sign in</h1>
        <p>Stay on track, elevate your fitness journey.</p>
        <form onSubmit={doLogin}>
          <Input
            label="Email"
            type="email"
            id="email"
            onFocus={() => setErrorMessage("")}
          />
          <Input
            label="Password"
            type="password"
            id="password"
            onFocus={() => setErrorMessage("")}
          />
          {errorMessage && <p className={classes.error}>{errorMessage}</p>}
          <Button type="submit" disabled={isLoading}>
            {isLoading ? "Loading..." : "Sign in"}
          </Button>
          <Link to="/request-password-reset">Forgot password?</Link>
        </form>
        <Seperator>Or</Seperator>
        <div className={classes.register}>
          Don't have an account? <Link to="/register">Register</Link>
        </div>
      </Box>
    </Layout>
  );
}

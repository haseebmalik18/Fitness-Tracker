import { Box } from "../../components/Box/Box";
import { Input } from "../../components/Input/Input";
import { Button } from "../../components/Button/Button";
import classes from "./Signup.module.scss";
import { Seperator } from "../../components/Seperator/Seperator";
import { Link, useNavigate } from "react-router-dom";
import { FormEvent, useState } from "react";
import { useAuthentication } from "../../contexts/AuthenticationContextProvider";
import { Layout } from "../../components/AuthenticationLayout/Layout";

export function Signup() {
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { register } = useAuthentication();
  const navigate = useNavigate();

  const doRegister = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;
    try {
      await register(email, password);
      navigate("/");
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessage(error.message);
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
        <h1>Sign Up</h1>
        <p>Maximize Your Potential, Track Every Step</p>
        <form onSubmit={doRegister}>
          <Input type="email" id="email" label="Email" />
          <Input type="password" id="password" label="Password" />
          {errorMessage && <p className={classes.error}>{errorMessage}</p>}
          <p className={classes.disclaimer}>
            By clicking Agree & Join or Continue, you agree to use blah{" "}
            <a href="">User Agreement</a>, <a href="">Privacy Policy</a>, and{" "}
            <a href="">Cookie Policy</a>
          </p>
          <Button type="submit" disabled={isLoading}>
            Agree & Join
          </Button>
        </form>
        <Seperator>Or</Seperator>
        <div className={classes.register}>
          Already on Blah? <Link to="/login">Sign in</Link>
        </div>
      </Box>
    </Layout>
  );
}

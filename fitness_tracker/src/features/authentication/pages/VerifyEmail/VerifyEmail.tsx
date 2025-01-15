import { useState } from "react";
import { Box } from "../../components/Box/Box";
import { Button } from "../../components/Button/Button";
import { Input } from "../../components/Input/Input";
import classes from "./VerifyEmail.module.scss";
import { useNavigate } from "react-router-dom";
import { Layout } from "../../components/AuthenticationLayout/Layout";

export function VerifyEmail() {
  const [errorMessage, setErrorMessage] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const valdiateEmail = async (code: string) => {
    setMessage("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/authentication/validate-email-verification-token?token=${code}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (response.ok) {
        setErrorMessage("");
        navigate("/");
      }
      const { message } = await response.json();
      setErrorMessage(message);
    } catch (e) {
      console.log(e);
      setErrorMessage("An unknown error occurred. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const sendEmailVerificationToken = async () => {
    setErrorMessage("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/authentication/send-email-verification-token`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (response.ok) {
        setErrorMessage("");
        setMessage("Verification code sent successfully");
        return;
      }
      const { message } = await response.json();
      setErrorMessage(message);
    } catch (e) {
      console.log(e);
      setErrorMessage("An unknown error occurred. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Verify your email </h1>
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            setIsLoading(true);
            const code = e.currentTarget.code.value;
            await valdiateEmail(code);
            setIsLoading(false);
          }}
        >
          <p>
            Only one step left to complete your registration. Verify your email
            address.
          </p>

          <Input type="text" label="Verification code" key="code" name="code" />
          {message && <p style={{ color: "green" }}>{message}</p>}
          {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}
          <Button type="submit" disabled={isLoading}>
            Verify
          </Button>
          <Button
            outline
            type="button"
            disabled={isLoading}
            onClick={() => {
              sendEmailVerificationToken();
            }}
          >
            Send again
          </Button>
        </form>
      </Box>
    </Layout>
  );
}

import React, { ReactNode } from "react";
import classes from "./Layout.module.scss";

export function Layout({
  children,
  className,
}: {
  children: ReactNode;
  className: string;
}) {
  return (
    <div className={`${classes.root} ${className}`}>
      <header className={classes.container2}>
        <a href="/">
          {/*<img src="/logo.svg" alt="Logo" className={classes.logo} /> */}
          <img src="/dumbbell2.png" alt="Dumbbell" className={classes.logo} />
        </a>
      </header>
      <main className={classes.container}>{children}</main>
    </div>
  );
}

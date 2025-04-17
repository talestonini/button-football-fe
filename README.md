# Button Football Front-End
This is the front-end to the Button Football app.

## Pre-requisites
- Java 21
- SBT

Launch SBT with the provided script `sbt.sh` for more memory.

## Running Unit Tests

In SBT:

    test

## Developing with Auto-Reload

In SBT:

    ~fastLinkJS

In another terminal, execute `dev_local.sh`, which essentially does:

    ./prep_public.sh public
    npm run dev

## Building for Deployment

These are the steps to build the final (optimised) version of the app for testing before deployment to Firebase Hosting
(ie, Vite preview).  For deployment per-se, they are not necessary, as everything is automated in script `deploy.sh`.

In SBT:

    ~fullLinkJS

In another terminal:

    ./prep_public.sh public   <-- must be before `npm run build`, as that
    npm run build             <-- places `public` artifacts in `dist`
    npm run preview

## Deploying

Have the JSON key file path of a Firebase service account pointed by an env var `GOOGLE_APPLICATION_CREDENTIALS` and:

    ./deploy.sh

## TODOs/FIXMEs

- In standings, the championship field is just the championship type, missing the edition.
- Delete counter.js.
- Logos for trophies.
- App icon.
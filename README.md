# DHBW-Multigrading-Toolkit

## Uses libraries
- Angular: [Website](https://angular.io/)

## Basic Workflow
### Prerequesites
You need the following pieces of software prior to using this guide:
- Node.js 18 LTS
### Installation
To build and use this application, you will need the angular-cli, which can be installed using the following command.

``
npm install -g @angular/cli
``

After this, you need to install the rest of the dependencies defined in package.json.

``
npm install
``

### Building the application
To generate the output files and get the application ready for web-preview, you may use the following command:

``
npm run build
``

To generate the Electron-Bundles and package the application for production, use:

``
npm run production
``

### Development-Workflow
To get a preview of the electron application for testing, you may refer to the following command:

``
npm run electron
``

To just view a live-webview (for example for UI only development), you may use the faster command:

``
npm run web
``

**Note:** Usually it is not neccessary to build the app for production on device, because all changes on the main branch are automatically built by CI/CD.

var _a = require("electron"), app = _a.app, BrowserWindow = _a.BrowserWindow;
var path = require("path");
var url = require("url");
var win;
function createWindow() {
    win = new BrowserWindow({ width: 800, height: 600 });
    // load the dist folder from Angular
    win.loadURL(url.format({
        pathname: path.join(__dirname, "/dist/index.html"),
        protocol: "file:",
        slashes: true
    }));
    // The following is optional and will open the DevTools:
    // win.webContents.openDevTools()
    win.on("closed", function () {
        win = null;
    });
}
app.on("ready", createWindow);
// on macOS, closing the window doesn't quit the app
app.on("window-all-closed", function () {
    if (process.platform !== "darwin") {
        app.quit();
    }
});

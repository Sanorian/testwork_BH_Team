#include "crow.h"
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xcursor/Xcursor.h>
#include <X11/cursorfont.h>
#include <stdio.h>
#include <string_view>
#include <iostream>
#include <unistd.h>
#include <array>
#include <algorithm>
#include <cmath>

#define _USE_MATH_DEFINES

struct CursorCoordinates{
    public:
        Cursor cursor;
        Window window;
        int x, y, centerX, centerY;
};

std::vector<CursorCoordinates> moveCursorsInCircle(std::vector<CursorCoordinates> cursorCoordinatesVector, Display *display, int radius, double angle) {
    for (CursorCoordinates cursorCoordinates: cursorCoordinatesVector) {
        int newX = floor(cursorCoordinates.centerX+radius*std::cos(angle));
        int newY = floor(cursorCoordinates.centerY+radius*std::sin(angle));
        cursorCoordinates.x = newX;
        cursorCoordinates.y = newY;
        XWarpPointer(display, None, cursorCoordinates.window, newX, newY, 0, 0, 100, 100);
    }
    return cursorCoordinatesVector;
}

CursorCoordinates createCenteredCursor(int x0, int y0, int width, int height, Display *display, Window rootWindow){
    CursorCoordinates cursorCoordinates;
    unsigned int shape = XC_left_ptr;
    Cursor cursor = XCreateFontCursor(display, shape);
    Window window = XCreateSimpleWindow(display, rootWindow, x0, y0, width, height, 0, 0, 0);
    XDefineCursor(display, window, cursor);
    XWarpPointer(display, None, window, x0+floor(width/2), y0+floor(height/2), 0, 0, 100, 100);

    cursorCoordinates.cursor = cursor;
    cursorCoordinates.window = window;
    cursorCoordinates.x = x0+floor(width/2);
    cursorCoordinates.y = y0+floor(height/2);
    cursorCoordinates.centerX = x0+floor(width/2);
    cursorCoordinates.centerY = y0+floor(height/2);

    return cursorCoordinates;
}

std::vector<CursorCoordinates> createAndMoveToBaseCursors(int numberOfCursors, Display &display, Window window, int browserWindowWidth, int browserWindowHeight, int screenWidth, int screenHeight){
    std::vector<CursorCoordinates> cursorCoordinatesVector(numberOfCursors);

    int x0=0, y0=0;

    for(int i=0; i<numberOfCursors; i++){
        cursorCoordinatesVector[i] = createCenteredCursor(x0, y0, browserWindowWidth, browserWindowHeight, &display, window);
        if (x0+browserWindowWidth>screenWidth) {
            x0=0;
            y0+=browserWindowHeight;
        } else {
            x0+=browserWindowWidth;
        }
    }

    return cursorCoordinatesVector;
}

int main(int argc, char** argv) {
    using namespace std::literals;

    int numberOfCursors = atoi(argv[1]);
    int x = atoi(argv[2]);

    double period = 2*M_PI/x;
    double angle = 0;

    Display *display = XOpenDisplay(NULL);
    Window rootWindow = DefaultRootWindow(display);
    if (display == NULL) {
        fprintf(stderr, "Cannot open display\n");
        return 1;
    }

    int screen = DefaultScreen(&display);
    int screenWidth = DisplayWidth(&display, screen);
    int screenHeight = DisplayHeight(&display, screen);

    int browserWindowWidth = floor(2*screenWidth/(numberOfCursors + numberOfCursors%2));
    int browserWindowHeight = floor(screenHeight/2);

    int radius = std::min(floor(browserWindowHeight/2), floor(browserWindowWidth/2));

    std::vector<CursorCoordinates> cursorCoordinatesVector = createAndMoveToBaseCursors(numberOfCursors, *display, rootWindow, browserWindowWidth, browserWindowHeight, screenWidth, screenHeight);

    XFlush(display);

    crow::SimpleApp app;

    CROW_ROUTE(app, "/")
    ([&display, &cursorCoordinatesVector, &radius, &angle, &period](crow::request& req, crow::response& res){
        cursorCoordinatesVector = moveCursorsInCircle(cursorCoordinatesVector, display, radius, angle);
        res.write(std::to_string(cursorCoordinatesVector[0].x) + " " + std::to_string(cursorCoordinatesVector[0].y));
        angle += period;
        return;
    });
    CROW_ROUTE(app, "/close")
    ([&display](crow::request& req, crow::response& res){
        XCloseDisplay(display);
    });
    app.port(18080).multithreaded().run();
}

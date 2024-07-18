#include "crow.h"
#include <X11/Xlib.h>
#include <X11/X.h>
#include <X11/Xcursor/Xcursor.h>
#include <stdio.h>
#include <unistd.h>
#include <array>
#include <algorithm>
#include <array>
#include <cmath>
#define _USE_MATH_DEFINES

void newCoordinates(int radius, int centerX, int centerY, int angle, int &x, int &y) {
    double radians = angle * M_PI / 180.0;
    x = centerX + radius * cos(radians);
    y = centerY + radius * sin(radians);
}

Cursor createCenteredCursor(int x0, int y0, int width, int height, Display *display, Window rootWindow){    Cursor cursor = XCreateFontCursor(display, XC_left_ptr);
    XDefineCursor(display, rootWindow, cursor);
    XWarpPointer(display, None, rootWindow, x0+floor(width/2), y0+floor(height/2), 0, 0, 100, 100);
    return cursor;
}

Cursor *createAndMoveToBaseCursors(int numberOfCursors, Display &display){
    std::array<Cursor, numberOfCursors> cursorsArray;

    int screen = DefaultScreen(display);
    int screenWidth = DisplayWidth(display, screen);
    int screenHeight = DisplayHeight(display, screen);

    int browserWindowWidth = floor(2*screenWidth/(numberOfCursors + numberOfCursors%2));
    int browserWindowHeight = floor(screenHeight/2);

    int x0=0, y0=0;

    for(int i=0; i<numberOfCursors; i++){
        cursorsArray[i] = createCenteredCursor(x0, y0, browserWindowWidth, browserWindowHeight);
        if (x0+browserWindowWidth>screenWidth){
            x0=0;
            y0+=browserWindowHeight;
        }else{
            x0+=browserWindowWidth;
        }
    }

    return cursorsArray.data();
}

int main() {
    Display *display = XOpenDisplay(NULL);
    Window rootWindow = DefaultRootWindow(display);
    if (display == NULL) {
        fprintf(stderr, "Cannot open display\n");
        return 1;
    }
    int x = 8;
    double time = 1.0;

    double period = time/x;
    int angle = 0;

    int numberOfCursors = 3;

    auto cursorsArray = createAndMoveToBaseCursors(numberOfCursors, display);

    XFlush(display);

    crow::SimpleApp app;

    CROW_ROUTE(app, "/")([](){
        Integer<int, 2> cursorCoordinates = getCursorCoordinates();
        return std::to_string(cursorCoordinates[0])+" "+std::to_string(cursorCoordinates[1]);
    });

    app.port(18080).run();
    // запустить движение курсоров по кругу
}
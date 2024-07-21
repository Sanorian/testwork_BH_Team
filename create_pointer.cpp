#include "crow.h"
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xcursor/Xcursor.h>
#include <stdio.h>
#include <string_view>
#include <iostream>
#include <unistd.h>
#include <array>
#include <algorithm>
#include <cmath>
#include <X11/cursorfont.h>

#define _USE_MATH_DEFINES

struct CursorCoordinates{
    Cursor cursor;
    int x, y, centerX, centerY;
}


void moveCursorsInCircle(std::vector<Cursor>& cursorsArray, Display* display, Window window, int centerX, int centerY, int radius, int delayMillis) {
    const int numCursors = cursorsArray.size();
    for (int angle = 0; angle <= 360; angle += 360 / numCursors) { // Distribute angles evenly
        std::array<int, 2> positions[numCursors]; // Store x, y positions for each cursor
        for (int i = 0; i < numCursors; ++i) {
            double radians = angle * M_PI / 180.0;
            positions[i][0] = centerX + radius * cos(radians);
            positions[i][1] = centerY + radius * sin(radians);
            XWarpPointer(display, None, window, 0, 0, 0, 0, positions[i][0], positions[i][1]);
        }
        usleep(delayMillis * 1000); // Delay for smooth animation
    }
}

Cursor createCenteredCursor(int x0, int y0, int width, int height, Display *display, Window rootWindow){
    CursorCoordinates cursorCoordinates;
    unsigned int shape = XC_left_ptr;
    Cursor cursor = XCreateFontCursor(display, shape);
    XDefineCursor(display, rootWindow, cursor);
    XWarpPointer(display, None, rootWindow, x0+floor(width/2), y0+floor(height/2), 0, 0, 100, 100);

    cursorCoordinates.cursor = cursor;
    cursorCoordinates.x = x0+floor(width/2);
    cursorCoordinates.y = y0+floor(height/2);
    cursorCoordinates.centerX = x0+floor(width/2);
    cursorCoordinates.centerY = y0+floor(height/2);

    cursorCoordinatesVector.push_back(cursorCoordinates);
    return cursor;
}

std::vector<Cursor> createAndMoveToBaseCursors(int numberOfCursors, Display &display, Window window){
    std::vector<Cursor> cursorsArray(numberOfCursors);

    int screen = DefaultScreen(&display);
    int screenWidth = DisplayWidth(&display, screen);
    int screenHeight = DisplayHeight(&display, screen);

    int browserWindowWidth = floor(2*screenWidth/(numberOfCursors + numberOfCursors%2));
    int browserWindowHeight = floor(screenHeight/2);

    int x0=0, y0=0;

    for(int i=0; i<numberOfCursors; i++){
        cursorsArray[i] = createCenteredCursor(x0, y0, browserWindowWidth, browserWindowHeight, &display, window);
        if (x0+browserWindowWidth>screenWidth) {
            x0=0;
            y0+=browserWindowHeight;
        } else {
            x0+=browserWindowWidth;
        }
    }

    return cursorsArray;
}

int main(int argc, char** argv) {
    std::vector<CursorCoordinates> cursorCoordinatesVector;
    using namespace std::literals;
    int numberOfCursors = atoi(argv[1]);
    int x = atoi(argv[2]);
    Display *display = XOpenDisplay(NULL);
    Window rootWindow = DefaultRootWindow(display);
    if (display == NULL) {
        fprintf(stderr, "Cannot open display\n");
        return 1;
    }

    auto cursorsArray = createAndMoveToBaseCursors(numberOfCursors, *display, rootWindow);

    XFlush(display);

    crow::SimpleApp app;

    CROW_ROUTE(app, "/")
    ([](crow::request& req, crow::response& res){
        int[] = getCoordinates();
        res.write("Hello world");
        return;
    });

    app.port(18080).multithreaded().run();
    // запустить движение курсоров по кругу

}

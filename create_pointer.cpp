#include <X11/Xlib.h>
#include <stdio.h>
#include <unistd.h>
#include <array>
#include <algorithm>
#include <cmath>
#define _USE_MATH_DEFINES

void newCoordinates(int radius, int centerX, int centerY, int angle, int &x, int &y) {
    double radians = angle * M_PI / 180.0;
    x = centerX + radius * cos(radians);
    y = centerY + radius * sin(radians);
}


int main() {
    Display *display = XOpenDisplay(NULL);
    if (display == NULL) {
        fprintf(stderr, "Cannot open display\n");
        return 1;
    }
    int x = 8;
    double time = 1.0;
    int screen = DefaultScreen(display);
    int width = DisplayWidth(display, screen);
    int height = DisplayHeight(display, screen);

    double period = time/x;
    int angle = 0;
    Window cursor1 = XCreateSimpleWindow(display, RootWindow(display, screen), 0, 0, 10, 10, 1,
                                          BlackPixel(display, screen), WhitePixel(display, screen));
    Window cursor2 = XCreateSimpleWindow(display, RootWindow(display, screen), 0, 0, 10, 10, 1,
                                          BlackPixel(display, screen), WhitePixel(display, screen));
    Window cursor3 = XCreateSimpleWindow(display, RootWindow(display, screen), 0, 0, 10, 10, 1,
                                          BlackPixel(display, screen), WhitePixel(display, screen));

    XMapWindow(display, cursor1);
    XMoveWindow(display, cursor1, width/4, height/4);
    XMapWindow(display, cursor2);
    XMoveWindow(display, cursor2, width/4, height/4*3);
    XMapWindow(display, cursor3);
    XMoveWindow(display, cursor3, width/4*3, height/4);

    XFlush(display);

    sleep(5);

    int x1,x2,x3,y1,y2,y3;

    int r = std::min(width/4, height/4);
    while(true) {
        newCoordinates(r, width/4, height/4, period, x1, y1);
        newCoordinates(r, width/4, height/4*3, period, x2, y2);
        newCoordinates(r, width/4*3, height/4, period, x3, y3);
        XMoveWindow(display, cursor1, x1, y1);
        XMoveWindow(display, cursor2, x2, y2);
        XMoveWindow(display, cursor3, x3, y3);
        angle += period;
        sleep(period);
    }

    XCloseDisplay(display);

    return 0;
}
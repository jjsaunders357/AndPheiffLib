#type VERTEX
#version 300 es
precision mediump float;
#const b1 true
#const b2 false
const int b4test=4;
#if b1
    const int b1test=1;
    #if !b2
        const int b2test=0;
    #else
        const int b2test=1;
        #if b3
            const int b3test=1;
        #endif
        #if !b3
            const int b3test=0;
        #endif
    #endif
#else
    const int b1test=0;
#endif

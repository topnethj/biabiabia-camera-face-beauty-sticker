precision highp float;
attribute vec4 position;
attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;
uniform mat3 texCoorMat;
uniform mat4 tranMat;

void main()
{
    gl_Position = position * tranMat;
    gl_Position.z = 0.0;
    
    vec3 coord =vec3(inputTextureCoordinate.xy,1.0);
    coord = coord * texCoorMat;
    textureCoordinate = coord.xy;
}

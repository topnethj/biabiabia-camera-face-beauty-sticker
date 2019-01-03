precision lowp float;

attribute vec4 position;
attribute vec4 inputTextureCoordinate;
varying vec2 textureCoordinate;

uniform bool enable;
const int leftPointIndex = 0;
const int rightPointIndex = 32;
const float aspectRatio = 0.56;


float getPointsDistance(vec2 point1,vec2 point2,float screenRatio)
{//注意：pow的第一个参数不能为负值，应该abs
    point1.y = point1.y/screenRatio;
    point2.y = point2.y/screenRatio;
    return distance(point1,point2);
}
void main()
{
    gl_Position = position ;//* roateMatZ;
    textureCoordinate = inputTextureCoordinate.xy;
}

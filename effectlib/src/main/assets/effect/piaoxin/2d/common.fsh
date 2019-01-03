precision highp float;
varying vec2 textureCoordinate;
uniform sampler2D inputImageTexture;
uniform float alphaFactor;

void main()
{
    gl_FragColor = texture2D(inputImageTexture, textureCoordinate) * alphaFactor;//腮红0.3
}

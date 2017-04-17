precision highp float;

varying vec2 textureCoordinate;
varying vec2 textureCoordinate2;
varying vec2 textureCoordinate3;

uniform sampler2D inputImageTexture;
uniform sampler2D inputImageTexture2;
uniform sampler2D inputImageTexture3;

vec4 blend(vec4 t1, vec4 t2)
{
    float alphaDivisor = t2.a + step(t2.a, 0.0);
    return vec4(mix(t1.rgb, t2.rgb / alphaDivisor, t2.a), t1.a);
}

void main()
{
    vec4 t1 = texture2D(inputImageTexture, textureCoordinate);
    vec4 t2 = texture2D(inputImageTexture2, textureCoordinate2);
    vec4 t3 = texture2D(inputImageTexture3, textureCoordinate3);
    gl_FragColor = blend(blend(t1, t2), t3);
}

  #extension GL_OES_EGL_image_external : require
  precision highp float;
   uniform samplerExternalOES uTexture;
   varying vec2 vTextureCoord;



   void main(){

       vec3 centralColor;
       float sampleColor;


       vec2 blurCoordinates[20];

       float mul = 2.0;

       float mul_x = mul / 720.0;
       float mul_y = mul / 1280.0;


       blurCoordinates[0] = vTextureCoord + vec2(0.0 * mul_x,-10.0 * mul_y);
       blurCoordinates[1] = vTextureCoord + vec2(5.0 * mul_x,-8.0 * mul_y);
       blurCoordinates[2] = vTextureCoord + vec2(8.0 * mul_x,-5.0 * mul_y);
       blurCoordinates[3] = vTextureCoord + vec2(10.0 * mul_x,0.0 * mul_y);
       blurCoordinates[4] = vTextureCoord + vec2(8.0 * mul_x,5.0 * mul_y);
       blurCoordinates[5] = vTextureCoord + vec2(5.0 * mul_x,8.0 * mul_y);
       blurCoordinates[6] = vTextureCoord + vec2(0.0 * mul_x,10.0 * mul_y);
       blurCoordinates[7] = vTextureCoord + vec2(-5.0 * mul_x,8.0 * mul_y);
       blurCoordinates[8] = vTextureCoord + vec2(-8.0 * mul_x,5.0 * mul_y);
       blurCoordinates[9] = vTextureCoord + vec2(-10.0 * mul_x,0.0 * mul_y);
       blurCoordinates[10] = vTextureCoord + vec2(-8.0 * mul_x,-5.0 * mul_y);
       blurCoordinates[11] = vTextureCoord + vec2(-5.0 * mul_x,-8.0 * mul_y);
       blurCoordinates[12] = vTextureCoord + vec2(0.0 * mul_x,-6.0 * mul_y);
       blurCoordinates[13] = vTextureCoord + vec2(-4.0 * mul_x,-4.0 * mul_y);
       blurCoordinates[14] = vTextureCoord + vec2(-6.0 * mul_x,0.0 * mul_y);
       blurCoordinates[15] = vTextureCoord + vec2(-4.0 * mul_x,4.0 * mul_y);
       blurCoordinates[16] = vTextureCoord + vec2(0.0 * mul_x,6.0 * mul_y);
       blurCoordinates[17] = vTextureCoord + vec2(4.0 * mul_x,4.0 * mul_y);
       blurCoordinates[18] = vTextureCoord + vec2(6.0 * mul_x,0.0 * mul_y);
       blurCoordinates[19] = vTextureCoord + vec2(4.0 * mul_x,-4.0 * mul_y);


       sampleColor = texture2D(uTexture, vTextureCoord).g * 22.0;

       sampleColor += texture2D(uTexture, blurCoordinates[0]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[1]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[2]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[3]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[4]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[5]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[6]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[7]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[8]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[9]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[10]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[11]).g;
       sampleColor += texture2D(uTexture, blurCoordinates[12]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[13]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[14]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[15]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[16]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[17]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[18]).g * 2.0;
       sampleColor += texture2D(uTexture, blurCoordinates[19]).g * 2.0;



       sampleColor = sampleColor/50.0;


       centralColor = texture2D(uTexture, vTextureCoord).rgb;

       float dis = centralColor.g - sampleColor + 0.5;


       if(dis <= 0.5)
       {
           dis = dis * dis * 2.0;
       }
       else
       {
           dis = 1.0 - ((1.0 - dis)*(1.0 - dis) * 2.0);
       }

       if(dis <= 0.5)
       {
           dis = dis * dis * 2.0;
       }
       else
       {
           dis = 1.0 - ((1.0 - dis)*(1.0 - dis) * 2.0);
       }

       if(dis <= 0.5)
       {
           dis = dis * dis * 2.0;
       }
       else
       {
           dis = 1.0 - ((1.0 - dis)*(1.0 - dis) * 2.0);
       }

       if(dis <= 0.5)
       {
           dis = dis * dis * 2.0;
       }
       else
       {
           dis = 1.0 - ((1.0 - dis)*(1.0 - dis) * 2.0);
       }

       if(dis <= 0.5)
       {
           dis = dis * dis * 2.0;
       }
       else
       {
           dis = 1.0 - ((1.0 - dis)*(1.0 - dis) * 2.0);
       }


       float aa= 1.03;
       vec3 smoothColor = centralColor*aa - vec3(dis)*(aa-1.0);

       float hue = dot(smoothColor, vec3(0.299,0.587,0.114));

       aa = 1.0 + pow(hue, 0.6)*0.1;
       smoothColor = centralColor*aa - vec3(dis)*(aa-1.0);

       smoothColor.r = clamp(pow(smoothColor.r, 0.8),0.0,1.0);
       smoothColor.g = clamp(pow(smoothColor.g, 0.8),0.0,1.0);
       smoothColor.b = clamp(pow(smoothColor.b, 0.8),0.0,1.0);


       vec3 lvse = vec3(1.0)-(vec3(1.0)-smoothColor)*(vec3(1.0)-centralColor);
       vec3 bianliang = max(smoothColor, centralColor);
       vec3 rouguang = 2.0*centralColor*smoothColor + centralColor*centralColor - 2.0*centralColor*centralColor*smoothColor;


       gl_FragColor = vec4(mix(centralColor, lvse, pow(hue, 0.6)), 1.0);
       gl_FragColor.rgb = mix(gl_FragColor.rgb, bianliang, pow(hue, 0.0));
       gl_FragColor.rgb = mix(gl_FragColor.rgb, rouguang, 0.25);



       mat3 saturateMatrix = mat3(
                                  1.1102,
                                  -0.0598,
                                  -0.061,
                                  -0.0774,
                                  1.0826,
                                  -0.1186,
                                  -0.0228,
                                  -0.0228,
                                  1.1772);

       vec3 satcolor = gl_FragColor.rgb * saturateMatrix;
       gl_FragColor.rgb = mix(gl_FragColor.rgb, satcolor, 0.25);


   }

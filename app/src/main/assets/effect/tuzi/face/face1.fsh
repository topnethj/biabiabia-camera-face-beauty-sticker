precision highp float;

varying vec2 textureCoordinate;

uniform sampler2D inputImageTexture;
const int ExtraCount = 128;
const int FacePointCount = 106;
uniform vec2 facePoints[FacePointCount * 4];
uniform float extra[ExtraCount];
uniform int faceStatus1;
uniform int faceStatus2;
uniform int faceStatus3;
uniform int faceStatus4;


uniform int faceCount;

uniform mat4 leftFourEyes;
uniform mat4 rightFourEyes;
uniform vec4 eyeWidths;
uniform vec4 eyeHeights;
uniform vec2 faceMorphCenter[4];
uniform vec2 faceMorphP1[4];
uniform vec2 faceMorphP3[4];
uniform vec2 leftPointLine[4];
uniform vec2 rightPointLine[4];
uniform vec2 triangleCenter[4];
uniform vec4 faceWidth;
uniform float aspectRatio;

const float eps = 0.0000001;

const int leftEyeIndex = 74;
const int rightEyeIndex = 77;

vec4 defCalVecAndVec(vec4 v1,vec4 v2)
{
    vec4 result;
    result.x = v1.x * v2.x;
    result.y = v1.y * v2.y;
    result.z = v1.z * v2.z;
    result.w = v1.w * v2.w;
    return result;
}

vec4 calDistanceMat(mat4 m1,mat4 m2)
{
    vec4 result;
    vec2 vecm1 = vec2(m1[0][0],m1[1][0]);
    vec2 vecm2 = vec2(m2[0][0],m2[1][0]);
    result.x = distance(vecm1,vecm2);
    
    vecm1.x = m1[0][1];
    vecm1.y = m1[1][1];
    
    vecm2.x = m2[0][1];
    vecm2.y = m2[1][1];
    result.y = distance(vecm1,vecm2);
    
    vecm1.x = m1[0][2];
    vecm1.y = m1[1][2];
    
    vecm2.x = m2[0][2];
    vecm2.y = m2[1][2];
    result.z = distance(vecm1,vecm2);
    
    vecm1.x = m1[0][3];
    vecm1.y = m1[1][3];
    
    vecm2.x = m2[0][3];
    vecm2.y = m2[1][3];
    result.w = distance(vecm1,vecm2);
    return result;
}

//根据确定一点计算指定距离的两点，此处对于旋转效果一样
//p1,p3确定竖直直线
//p2为椭圆中心点，即变形的中心点
vec2 testCal(vec2 p1,vec2 p2,vec2 p3,inout vec2 top,inout vec2 left,vec2 offset,float aspectRatio)
{
    vec2 p1ToUse = vec2(p1.x,p1.y/aspectRatio);
    vec2 p2ToUse = vec2(p2.x,p2.y/aspectRatio);
    vec2 p3ToUse = vec2(p3.x,p3.y/aspectRatio);
    float dis = distance(p2ToUse,p3ToUse);//43点到中心点的距离
    //纵轴
    float a11 = p2ToUse.y - p1ToUse.y;
    float b11 = p1ToUse.x - p2ToUse.x;
    
    float sqrtValue = sqrt(pow(abs(a11),2.) + pow(abs(b11),2.)) + eps;
    float x33 = p2ToUse.x - b11 * offset.y/sqrtValue;
    float y33 = p2ToUse.y + a11 * offset.y/sqrtValue;
    
    //横轴
    float a22 = b11;
    float b22 = -a11;
    
    float sqrtValue2 = sqrt(pow(abs(a22),2.) + pow(abs(b22),2.)) + eps;
    vec2 result;
    result.x = x33 - b22 * offset.x/sqrtValue2;
    result.y = y33 + a22 * offset.x/sqrtValue2;
    
    top.x = result.x + b11 * dis/sqrtValue;
    top.y = result.y - a11 * dis/sqrtValue;
    
    left.x = result.x + b22 * 2.0 * dis/sqrtValue2;
    left.y = result.y - a22 * 2.0 * dis/sqrtValue2;
    
    result.y = result.y * aspectRatio;
    top.y = top.y * aspectRatio;
    left.y = left.y * aspectRatio;
    return result;
}

vec2 expandTexture(mat4 centerPostion,vec2 currentTexture,float aspectRatio,vec4 width,vec4 height,float Strength,float flag)
{
    vec2 result = currentTexture;
    mat4 centerPostionToUse = centerPostion;
    centerPostionToUse[1] = centerPostionToUse[1]/aspectRatio;
    vec2 currentPositionToUse = vec2(currentTexture.x,currentTexture.y/aspectRatio);
    
    float xoffset = currentPositionToUse.x - centerPostionToUse[0][0];
    float yoffset = currentPositionToUse.y - centerPostionToUse[1][0];
    
    float radius = pow(abs(xoffset),2.)/(pow(abs(width.x),2.) + eps) + pow(abs(yoffset),2.)/(pow(abs(height.x),2.0) + eps);
    
    float ScaleFactor = 0.;
    //第一张脸
    if(radius < 1.)
    {
        ScaleFactor = Strength * (radius - 1.) * flag;
        result = currentTexture + vec2(xoffset,yoffset) * ScaleFactor;
    }
    
    vec2 resultToUse = result;
    resultToUse.y = resultToUse.y/aspectRatio;
    xoffset = resultToUse.x - centerPostionToUse[0][1];
    yoffset = resultToUse.y - centerPostionToUse[1][1];
    
    radius = pow(abs(xoffset),2.)/(pow(abs(width.y),2.) + eps) + pow(abs(yoffset),2.)/(pow(abs(height.y),2.0) + eps);
    //第二张脸
    if (radius < 1.) {
        ScaleFactor = Strength * (radius - 1.) * flag;
        result = result + vec2(xoffset,yoffset) * ScaleFactor;
    }
    
    resultToUse = result;
    resultToUse.y = resultToUse.y/aspectRatio;
    xoffset = resultToUse.x - centerPostionToUse[0][2];
    yoffset = resultToUse.y - centerPostionToUse[1][2];
    
    radius = pow(abs(xoffset),2.)/(pow(abs(width.z),2.) + eps) + pow(abs(yoffset),2.)/(pow(abs(height.z),2.0) + eps);
    //第三张脸
    if (radius < 1.) {
        ScaleFactor = Strength * (radius - 1.) * flag;
        result = result + vec2(xoffset,yoffset) * ScaleFactor;
    }
    
    resultToUse = result;
    resultToUse.y = resultToUse.y/aspectRatio;
    xoffset = resultToUse.x - centerPostionToUse[0][3];
    yoffset = resultToUse.y - centerPostionToUse[1][3];
    
    radius = pow(abs(xoffset),2.)/(pow(abs(width.w),2.) + eps) + pow(abs(yoffset),2.)/(pow(abs(height.w),2.0) + eps);
    //第四张脸
    if (radius < 1.) {
        ScaleFactor = Strength * (radius - 1.) * flag;
        result = result + vec2(xoffset,yoffset) * ScaleFactor;
    }
    return result;
}


//收缩或膨胀(横向或者椭圆)  xOrXY = 0|1
//收缩或膨胀(横向或者椭圆)  xOrXY = 0|1
vec2 expandXTextureRotate(vec2 currentTexture,mat4 point1,mat4 center,mat4 point3,float aspectRatio,float Strength,float StrengthY,float flag,float aScale,float bScale,float xOrXY)
{
    vec2 currentPositionToUse = currentTexture;
    currentPositionToUse.y = currentPositionToUse.y/aspectRatio;
    
    mat4 point1ToUse = point1;
    mat4 centerPostionToUse = center;
    mat4 point3ToUse = point3;
    
    point1ToUse[1] = point1ToUse[1]/aspectRatio;
    centerPostionToUse[1] = centerPostionToUse[1]/aspectRatio;
    point3ToUse[1] = point3ToUse[1]/aspectRatio;
    
    vec2 result = currentTexture;
    //横轴直线方程参数
    vec4 a1= centerPostionToUse[1] - point1ToUse[1];
    vec4 b1= point1ToUse[0] - centerPostionToUse[0];
    vec4 c1 = defCalVecAndVec(point1ToUse[0],(point1ToUse[1] - centerPostionToUse[1])) - defCalVecAndVec(point1ToUse[1],(point1ToUse[0] - centerPostionToUse[0]));
    
    
    //纵轴直线方程
    vec4 a2=point3ToUse[1]-centerPostionToUse[1];
    vec4 b2=centerPostionToUse[0]-point3ToUse[0];
    vec4 c2 = defCalVecAndVec(centerPostionToUse[0],(centerPostionToUse[1]-point3ToUse[1])) - defCalVecAndVec(centerPostionToUse[1],(centerPostionToUse[0]-point3ToUse[0]));
    
    //各轴求算
    vec4 azhou = aScale * calDistanceMat(point1ToUse,centerPostionToUse);
    vec4 bzhou = bScale * calDistanceMat(point3ToUse,centerPostionToUse);
    
    float a1Value = a1.x*a1.x+b1.x*b1.x + eps;
    float b1Value = a2.x*a2.x+b2.x*b2.x + eps;
    float azhoupow = pow(abs(azhou.x),2.) + eps;
    float bzhoupow = pow(abs(bzhou.x),2.) + eps;
    //第一张人脸
    float al=abs(a1.x*currentPositionToUse.x+b1.x*currentPositionToUse.y+c1.x)/sqrt(a1Value);
    float bl=abs(a2.x*currentPositionToUse.x+b2.x*currentPositionToUse.y+c2.x)/sqrt(b1Value);
    
    float aValue = pow(abs(bl),2.)/azhoupow;
    float bValue = pow(abs(al),2.)/bzhoupow;
    
    float ofl = aValue + bValue;
    if(ofl > 0. && ofl<=1.0)
    {
        vec2 offset = currentPositionToUse - vec2(centerPostionToUse[0][0],centerPostionToUse[1][0]);
        //横向变形，垂足计算
        float cx=(b2.x*b2.x*currentPositionToUse.x-a2.x*b2.x*currentPositionToUse.y-a2.x*c2.x)/b1Value;
        float cy=(a2.x*a2.x*currentPositionToUse.y-a2.x*b2.x*currentPositionToUse.x-b2.x*c2.x)/b1Value;
        offset.x = currentPositionToUse.x - xOrXY * cx;
        offset.y = currentPositionToUse.y - xOrXY * cy;
        
        float ScaleFactor = flag * Strength * (ofl - 1.0);
        result = result + offset * ScaleFactor;
    }
    
    
    currentPositionToUse.x = result.x;
    currentPositionToUse.y = result.y/aspectRatio;
    //第二张人脸
    a1Value = (a1.y*a1.y+b1.y*b1.y) + eps;
    b1Value = (a2.y*a2.y+b2.y*b2.y) + eps;
    azhoupow = pow(abs(azhou.y),2.) + eps;
    bzhoupow = pow(abs(bzhou.y),2.) + eps;
    al=abs(a1.y*currentPositionToUse.x+b1.y*currentPositionToUse.y+c1.y)/sqrt(a1Value);
    bl=abs(a2.y*currentPositionToUse.x+b2.y*currentPositionToUse.y+c2.y)/sqrt(b1Value);
    
    aValue = pow(abs(bl),2.)/azhoupow;
    bValue = pow(abs(al),2.)/bzhoupow;
    
    ofl = aValue + bValue;
    if(ofl > 0. && ofl<=1.0)
    {
        vec2 offset = currentPositionToUse - vec2(centerPostionToUse[0][1],centerPostionToUse[1][1]);
        //横向变形，垂足计算
        float cx=(b2.y*b2.y*currentPositionToUse.x-a2.y*b2.y*currentPositionToUse.y-a2.y*c2.y)/b1Value;
        float cy=(a2.y*a2.y*currentPositionToUse.y-a2.y*b2.y*currentPositionToUse.x-b2.y*c2.y)/b1Value;
        offset.x = currentPositionToUse.x - xOrXY * cx;
        offset.y = currentPositionToUse.y - xOrXY * cy;
        
        float ScaleFactor = flag * Strength * (ofl - 1.0);
        result = result + offset * ScaleFactor;
    }
    
    currentPositionToUse.x = result.x;
    currentPositionToUse.y = result.y/aspectRatio;
    //第三张人脸
    a1Value = (a1.z*a1.z+b1.z*b1.z) + eps;
    b1Value = (a2.z*a2.z+b2.z*b2.z) + eps;
    azhoupow = pow(abs(azhou.z),2.) + eps;
    bzhoupow = pow(abs(bzhou.z),2.) + eps;
    al=abs(a1.z*currentPositionToUse.x+b1.z*currentPositionToUse.y+c1.z)/sqrt(a1Value);
    bl=abs(a2.z*currentPositionToUse.x+b2.z*currentPositionToUse.y+c2.z)/sqrt(b1Value);
    
    aValue = pow(abs(bl),2.)/azhoupow;
    bValue = pow(abs(al),2.)/bzhoupow;
    
    ofl = aValue + bValue;
    if(ofl > 0. && ofl<=1.0)
    {
        vec2 offset = currentPositionToUse - vec2(centerPostionToUse[0][2],centerPostionToUse[1][2]);
        //横向变形，垂足计算
        float cx=(b2.z*b2.z*currentPositionToUse.x-a2.z*b2.z*currentPositionToUse.y-a2.z*c2.z)/b1Value;
        float cy=(a2.z*a2.z*currentPositionToUse.y-a2.z*b2.z*currentPositionToUse.x-b2.z*c2.z)/b1Value;
        offset.x = currentPositionToUse.x - xOrXY * cx;
        offset.y = currentPositionToUse.y - xOrXY * cy;
        
        float ScaleFactor = flag * Strength * (ofl - 1.0);
        result = result + offset * ScaleFactor;
    }
    
    
    currentPositionToUse.x = result.x;
    currentPositionToUse.y = result.y/aspectRatio;
    //第四张人脸
    a1Value = (a1.w*a1.w+b1.w*b1.w) + eps;
    b1Value = (a2.w*a2.w+b2.w*b2.w) + eps;
    azhoupow = pow(abs(azhou.w),2.) + eps;
    bzhoupow = pow(abs(bzhou.w),2.) + eps;
    al=abs(a1.w*currentPositionToUse.x+b1.w*currentPositionToUse.y+c1.w)/sqrt(a1Value);
    bl=abs(a2.w*currentPositionToUse.x+b2.w*currentPositionToUse.y+c2.w)/sqrt(b1Value);
    
    aValue = pow(abs(bl),2.)/azhoupow;
    bValue = pow(abs(al),2.)/bzhoupow;
    
    ofl = aValue + bValue;
    if(ofl > 0. && ofl<=1.0)
    {
        vec2 offset = currentPositionToUse - vec2(centerPostionToUse[0][3],centerPostionToUse[1][3]);
        //横向变形，垂足计算
        float cx=(b2.w*b2.w*currentPositionToUse.x-a2.w*b2.w*currentPositionToUse.y-a2.w*c2.w)/b1Value;
        float cy=(a2.w*a2.w*currentPositionToUse.y-a2.w*b2.w*currentPositionToUse.x-b2.w*c2.w)/b1Value;
        offset.x = currentPositionToUse.x - xOrXY * cx;
        offset.y = currentPositionToUse.y - xOrXY * cy;
        
        float ScaleFactor = flag * Strength * (ofl - 1.0);
        result = result + offset * ScaleFactor;
    }
    
    return result;
}
//point1  45
//point2  16
vec2 expandYTexture(vec2 currentTexture,vec2 point1,vec2 point2,float aspectRatio,float Strength,float height,float flag)
{
    vec2 result = currentTexture;
    point1.y = point1.y/aspectRatio;
    point2.y = point2.y/aspectRatio;
    vec2 curpoint = currentTexture;
    curpoint.y = curpoint.y/aspectRatio;
    //纵轴直线方程参数
    float a1=point2.y-point1.y;
    float b1=point1.x-point2.x;
    float c1=point1.x*(point1.y-point2.y)-point1.y*(point1.x-point2.x);
    // 中心点坐标求算
    float x=point1.x;
    float y=point1.y;
    // 底部点求算
    float x3=point2.x + (point2.x-point1.x)*0.5;
    float y3 = point2.y + (point2.y-point1.y) * height;
    //上边界直线方程
    float a2 = b1;
    float b2 = a1*-1.0;
    float c2 = a1*(point1.y-b1/(a1+eps)*point1.x);
    //下边界直线方程
    float a3=b1;
    float b3=-a1;
    float c3=a1*(y3-b1/(a1+eps)*x3);
    //各轴求算
    float bzhouValue = a2*a2+b2*b2;
    float bzhou = abs(a2*x3+b2*y3+c2)/(sqrt(bzhouValue) + eps);
    
    if(a2*curpoint.x+b2*curpoint.y+c2<0.0 && a3*curpoint.x+b3*curpoint.y+c3>0.0)
    {
        float cx,cy,bl,ofl;
        
        //        if (bzhouValue < 0.002) {
        //            cx = curpoint.x;
        //            cy = curpoint.y;
        //            bl = ofl = 1.0;
        //        }else{
        cx=(b2*b2*curpoint.x-a2*b2*curpoint.y-a2*c2)/(bzhouValue + eps);
        cy=(a2*a2*curpoint.y-a2*b2*curpoint.x-b2*c2)/(bzhouValue + eps);
        bl= abs(a2*curpoint.x+b2*curpoint.y+c2)/sqrt((bzhouValue + eps));
        ofl=bl*bl/(bzhou*bzhou + eps);
        //        }
        float xoffset = curpoint.x - cx;
        float yoffset = curpoint.y - cy;
        float ScaleFactor = -0.1*Strength*(ofl-1.0);
        
        result = result + vec2(xoffset,yoffset) * ScaleFactor;
    }
    
    return result;
}

float getPointsDistance(vec2 point1,vec2 point2,float screenRatio)
{//注意：pow的第一个参数不能为负值，应该abs
    point1.y = point1.y/screenRatio;
    point2.y = point2.y/screenRatio;
    return distance(point1,point2);
}
vec2  showFaceTest(vec2 currentTexture,float Strengtheye,float xscale,float yscale,float flag,float StrengthX,float StrengthY,float xoffset,float yoffset,float width,float height)
{
    vec2 result = currentTexture;
    
    vec4 eyeWidth = eyeWidths * xscale * 0.5 ;
    vec4 eyeHeight = eyeHeights * yscale * 0.5;
    
    //大眼睛
    result =expandTexture(leftFourEyes,result,aspectRatio,eyeWidth,eyeHeight,Strengtheye,flag);
    result = expandTexture(rightFourEyes,result,aspectRatio,eyeWidth,eyeHeight,Strengtheye,flag);
    
    
    //瘦脸——第一张
    vec2 offset1 = vec2(xoffset * faceWidth.x,yoffset * faceWidth.x);
    vec2 left1,top1;
    vec2 center1 = testCal(faceMorphP1[0],faceMorphCenter[0],faceMorphP3[0],top1,left1,offset1,aspectRatio);
    
    mat4 left,center,top;
    left[0][0] = left1.x;
    left[1][0] = left1.y;
    center[0][0] = center1.x;
    center[1][0] = center1.y;
    top[0][0] = top1.x;
    top[1][0] = top1.y;
    
    //瘦脸——第二张
    offset1 = vec2(xoffset * faceWidth.y,yoffset * faceWidth.y);
    center1 = testCal(faceMorphP1[1],faceMorphCenter[1],faceMorphP3[1],top1,left1,offset1,aspectRatio);
    left[0][1] = left1.x;
    left[1][1] = left1.y;
    center[0][1] = center1.x;
    center[1][1] = center1.y;
    top[0][1] = top1.x;
    top[1][1] = top1.y;
    
    //瘦脸——第三张
    offset1 = vec2(xoffset * faceWidth.z,yoffset * faceWidth.z);
    center1 = testCal(faceMorphP1[2] ,faceMorphCenter[2],faceMorphP3[2],top1,left1,offset1,aspectRatio);
    left[0][2] = left1.x;
    left[1][2] = left1.y;
    center[0][2] = center1.x;
    center[1][2] = center1.y;
    top[0][2] = top1.x;
    top[1][2] = top1.y;
    
    //瘦脸——第四张
    offset1 = vec2(xoffset * faceWidth.w,yoffset * faceWidth.w);
    center1 = testCal(faceMorphP1[3],faceMorphCenter[3],faceMorphP3[3],top1,left1,offset1,aspectRatio);
    left[0][3] = left1.x;
    left[1][3] = left1.y;
    center[0][3] = center1.x;
    center[1][3] = center1.y;
    top[0][3] = top1.x;
    top[1][3] = top1.y;
    
    //收缩，最后一个参数为1.0，表示只对x进行变化
    result = expandXTextureRotate(result,left,center,top,aspectRatio,StrengthX,StrengthY,-1.0,width,height,1.0);
    
    return result;
}

//扁下巴
vec2 ShowFaceEffectEx(vec2 currentTexture,int faceindex,float Strength,float xoffset,float yoffset,float width,float height)
{
    vec2 result = currentTexture;
    
    vec2 point1 = facePoints[45 + faceindex * FacePointCount]* 0.5 + 0.5;
    vec2 point2 = facePoints[16 + faceindex * FacePointCount] * 0.5 + 0.5;
    
    result = expandYTexture(currentTexture,point1,point2,aspectRatio,Strength,height,-1.0);
    return result;
}
void main()
{
    float eyeFlag = extra[0];
    float eyexScale = extra[1];
    float eyeyScale = extra[2];
    float eyeStrength = extra[3];
    float faceFlag = extra[4];
    float facexoffset = extra[5];
    float faceyoffset = extra[6];
    float facewidth = extra[7];
    float faceheight = extra[8];
    float faceStrength = extra[9];
    float faceStrengthY = extra[10];
    float rectHeight = extra[11];//矩形高度
    
    
    vec2 currentTexture = textureCoordinate;
    if (faceCount > 0)
    {
        eyeFlag = step(0.0,eyeFlag) * 2.0 - 1.0;//判断eyeFlag = -1还是1
        
        currentTexture = showFaceTest(currentTexture,eyeStrength,eyexScale,eyeyScale,eyeFlag,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
        
        currentTexture = ShowFaceEffectEx(currentTexture,0,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
        
        if(faceCount == 2)
        {
            currentTexture = ShowFaceEffectEx(currentTexture,1,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
        }
        else if(faceCount == 3)
        {
            currentTexture = ShowFaceEffectEx(currentTexture,1,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            currentTexture = ShowFaceEffectEx(currentTexture,2,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
        }
        else if(faceCount == 4)
        {
            currentTexture = ShowFaceEffectEx(currentTexture,1,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            currentTexture = ShowFaceEffectEx(currentTexture,2,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            currentTexture = ShowFaceEffectEx(currentTexture,3,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
        }
    }
    gl_FragColor = texture2D(inputImageTexture,currentTexture);
}

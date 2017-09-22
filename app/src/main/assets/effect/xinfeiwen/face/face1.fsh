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

const float aspectRatio = 0.56;

const int leftEyeIndex = 74;
const int rightEyeIndex = 77;

//根据确定一点计算指定距离的两点，此处对于旋转效果一样
//p1,p3确定竖直直线
//p2为椭圆中心点，即变形的中心点
vec2 testCal(vec2 p1,vec2 p2,vec2 p3,inout vec2 top,inout vec2 left,vec2 allss,vec2 offset,float aspectRatio)
{
    vec2 allssToUse = vec2(allss.x,allss.y/aspectRatio);
    vec2 p1ToUse = vec2(p1.x,p1.y/aspectRatio);
    vec2 p2ToUse = vec2(p2.x,p2.y/aspectRatio);
    vec2 p3ToUse = vec2(p3.x,p3.y/aspectRatio);
    float dis = distance(p2ToUse,p3ToUse);//43点到中心点的距离
    //纵轴
    float a11 = p2ToUse.y - p1ToUse.y;
    float b11 = p1ToUse.x - p2ToUse.x;
    //float c11 = noseP1.x *(noseP1.y - xiabaP2.y) - noseP1.y *(noseP1.x - xiabaP2.x);
    
    float x33 = p2ToUse.x - b11 * offset.y/sqrt(a11 * a11 + b11 * b11);
    float y33 = p2ToUse.y + a11 * offset.y/sqrt(a11 * a11 + b11 * b11);
    
    //横轴
    float a22 = b11;
    float b22 = -a11;
    //float c22 = a11 * (y33 - b11/a11 * x33);
    
    vec2 result;
    result.x = x33 - b22 * offset.x/sqrt(a22 * a22 + b22 * b22);
    result.y = y33 + a22 * offset.x/sqrt(a22 * a22 + b22 * b22);
    
    top.x = result.x + b11 * dis/sqrt(a11 * a11 + b11 * b11);
    top.y = result.y - a11 * dis/sqrt(a11 * a11 + b11 * b11);
    
    left.x = result.x + b22 * 2.0 * dis/sqrt(a22 * a22 + b22 * b22);
    left.y = result.y - a22 * 2.0 * dis/sqrt(a22 * a22 + b22 * b22);
    
    
    float dis1 = distance(result,allssToUse);
    float dis2 = distance(top,allssToUse);
    float dis3 = distance(left,allssToUse);
    
    result.y = result.y * aspectRatio;
    top.y = top.y * aspectRatio;
    left.y = left.y * aspectRatio;
    return result;
}
vec2 expandTexture(vec2 centerPostion,vec2 currentTexture,float aspectRatio,float width,float height,float Strength,float flag)
{
    vec2 result = currentTexture;
    vec2 currentPositionToUse = vec2(currentTexture.x,currentTexture.y/aspectRatio);
    vec2 centerPostionToUse = vec2(centerPostion.x,centerPostion.y/aspectRatio);
    float xoffset = currentPositionToUse.x - centerPostionToUse.x;
    float yoffset = currentPositionToUse.y - centerPostionToUse.y;
    float radius = (xoffset*xoffset)/(width*width)+(yoffset*yoffset)/(height*height);
    if(radius<=1.0)
    {
        float ScaleFactor = Strength*(radius-1.0)*flag;
        result.x = currentTexture.x + xoffset*ScaleFactor;
        result.y = currentTexture.y + yoffset*ScaleFactor;
    }
    
    return result;
}

//收缩或膨胀(横向或者椭圆)
vec2 expandXTextureRotate(vec2 currentTexture,vec2 point1,vec2 center,vec2 point3,float aspectRatio,float Strength,float StrengthY,float flag,float aScale,float bScale,float xOrXY)
{
    vec2 currentPositionToUse = vec2(currentTexture.x,currentTexture.y/aspectRatio);
    
    vec2 point1ToUse = vec2(point1.x,point1.y/aspectRatio);
    vec2 centerPostionToUse = vec2(center.x,center.y/aspectRatio);
    vec2 point3ToUse = vec2(point3.x,point3.y/aspectRatio);
    
    vec2 result = currentTexture;
    //横轴直线方程参数
    float a1= centerPostionToUse.y - point1ToUse.y;
    float b1= point1ToUse.x - centerPostionToUse.x;
    float c1 = point1ToUse.x*(point1ToUse.y - centerPostionToUse.y) - point1ToUse.y*(point1ToUse.x - centerPostionToUse.x);
    
    
    //纵轴直线方程
    float a2=point3ToUse.y-centerPostionToUse.y;
    float b2=centerPostionToUse.x-point3ToUse.x;
    float c2=centerPostionToUse.x*(centerPostionToUse.y-point3ToUse.y)-centerPostionToUse.y*(centerPostionToUse.x-point3ToUse.x);
    //各轴求算
    float azhou=aScale * distance(point1ToUse,centerPostionToUse);
    float bzhou=bScale * distance(point3ToUse,centerPostionToUse);
    
    float al=abs(a1*currentPositionToUse.x+b1*currentPositionToUse.y+c1)/sqrt(a1*a1+b1*b1);
    float bl=abs(a2*currentPositionToUse.x+b2*currentPositionToUse.y+c2)/sqrt(a2*a2+b2*b2);
    
    float ofl=(bl*bl)/(azhou*azhou) + (al*al)/(bzhou*bzhou);
    if(ofl<=1.0)
    {
        vec2 offset = currentPositionToUse - centerPostionToUse;
        if (xOrXY == 1.0)
        {//横向
            //垂足计算
            float cx=(b2*b2*currentPositionToUse.x-a2*b2*currentPositionToUse.y-a2*c2)/(a2*a2+b2*b2);
            float cy=(a2*a2*currentPositionToUse.y-a2*b2*currentPositionToUse.x-b2*c2)/(a2*a2+b2*b2);
            offset.x = currentPositionToUse.x - cx;
            offset.y = currentPositionToUse.y - cy;
        }
        
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
    float c2 = a1*(point1.y-b1/a1*point1.x);
    //下边界直线方程
    float a3=b1;
    float b3=-a1;
    float c3=a1*(y3-b1/a1*x3);
    //各轴求算
    float bzhou=abs(a2*x3+b2*y3+c2)/sqrt(a2*a2+b2*b2);
    if(a2*curpoint.x+b2*curpoint.y+c2<0.0 && a3*curpoint.x+b3*curpoint.y+c3>0.0)
    {
        float cx=(b2*b2*curpoint.x-a2*b2*curpoint.y-a2*c2)/(a2*a2+b2*b2);
        float cy=(a2*a2*curpoint.y-a2*b2*curpoint.x-b2*c2)/(a2*a2+b2*b2);
        float xoffset = curpoint.x - cx;
        float yoffset = curpoint.y - cy;
        float bl=abs(a2*curpoint.x+b2*curpoint.y+c2)/sqrt(a2*a2+b2*b2);
        float ofl=bl*bl/(bzhou*bzhou);
        float ScaleFactor = -0.1*Strength*(ofl-1.0);
        result.x = result.x + xoffset*ScaleFactor;
        result.y = result.y + yoffset*ScaleFactor;
    }
    
    return result;
}
float getPointsDistance(vec2 point1,vec2 point2,float screenRatio)
{//注意：pow的第一个参数不能为负值，应该abs
    point1.y = point1.y/screenRatio;
    point2.y = point2.y/screenRatio;
    return distance(point1,point2);
}

vec2 ShowEyeEffect(vec2 currentTexture,int faceindex,float Strength,float xscale,float yscale,float flag)
{
    vec2 result = currentTexture;
    vec2 leftEye = facePoints[leftEyeIndex + faceindex * FacePointCount] * 0.5 + 0.5;
    vec2 rightEye = facePoints[rightEyeIndex + faceindex * FacePointCount] * 0.5 + 0.5;
    float eyeWidth = (facePoints[55 + faceindex * FacePointCount].x - facePoints[52 + faceindex * FacePointCount].x)*xscale/2.0;
    float eyeHeight = (facePoints[73 + faceindex * FacePointCount].y - facePoints[72 + faceindex * FacePointCount].y)*yscale/2.0;
    
    result =expandTexture(leftEye,result,aspectRatio,eyeWidth,eyeHeight,Strength,flag);
    result = expandTexture(rightEye,result,aspectRatio,eyeWidth,eyeHeight,Strength,flag);
    
    return result;
}

//瘦脸
vec2 ShowFaceEffect(vec2 currentTexture,int faceindex,float Strength,float StrengthY,float xoffset,float yoffset,float width,float height)
{
    vec2 result = currentTexture;
    
    float dis = getPointsDistance(facePoints[0 + faceindex * FacePointCount],facePoints[32 + faceindex * FacePointCount],aspectRatio);
    vec2 offset = vec2(xoffset * dis,yoffset * dis);
    
    vec2 center = facePoints[46 + faceindex * FacePointCount] * 0.5 + 0.5;
    vec2 p1 = facePoints[45 + faceindex * FacePointCount] * 0.5 + 0.5;
    vec2 p3 = facePoints[43 + faceindex * FacePointCount] * 0.5 + 0.5;
    
    vec2 left,top;
    center = testCal(p1,center,p3,top,left,currentTexture,offset,aspectRatio);
    
    //收缩，最后一个参数为1.0，表示只对x进行变化
    result = expandXTextureRotate(result,left,center,top,aspectRatio,Strength,StrengthY,-1.0,width,height,1.0);
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
    
    int curFaceIndex = 0;
    
    vec2 currentTexture = textureCoordinate;
    if(faceStatus4 == 1 && faceStatus3 == 1 && faceStatus2 == 1 && faceStatus1 == 1){
        gl_FragColor = texture2D(inputImageTexture,textureCoordinate);
    }else{
        eyeFlag = step(0.0,eyeFlag) * 2.0 - 1.0;//判断eyeFlag = -1还是1
        if(faceStatus1 != 1)
        {
            curFaceIndex = 0;
            currentTexture = ShowEyeEffect(currentTexture,curFaceIndex,eyeStrength,eyexScale,eyeyScale,eyeFlag);
            
            if(faceFlag == 1.0)
            {
                //瘦脸
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
                
            }
            else if(faceFlag == 2.0)
            {
                //瘦脸
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
                
                //扁下巴
                currentTexture = ShowFaceEffectEx(currentTexture,curFaceIndex,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            }
        }
        if(faceStatus2 != 1)
        {
            curFaceIndex = 1;
            currentTexture = ShowEyeEffect(currentTexture,curFaceIndex,eyeStrength,eyexScale,eyeyScale,eyeFlag);
            
            if(faceFlag == 1.0)
            {
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
            }
            else if(faceFlag == 2.0)
            {
                //瘦脸
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
                
                //扁下巴
                currentTexture = ShowFaceEffectEx(currentTexture,curFaceIndex,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            }
        }
        
        if(faceStatus3 != 1)
        {
            curFaceIndex = 2;
            currentTexture = ShowEyeEffect(currentTexture,curFaceIndex,eyeStrength,eyexScale,eyeyScale,eyeFlag);
            
            if(faceFlag == 1.0)
            {
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
            }
            else if(faceFlag == 2.0)
            {
                //瘦脸
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
                
                //扁下巴
                currentTexture = ShowFaceEffectEx(currentTexture,curFaceIndex,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            }
        }
        if(faceStatus4 != 1)
        {
            curFaceIndex = 3;
            currentTexture = ShowEyeEffect(currentTexture,curFaceIndex,eyeStrength,eyexScale,eyeyScale,eyeFlag);
            
            if(faceFlag == 1.0)
            {
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
            }
            else if(faceFlag == 2.0)
            {
                //瘦脸
                currentTexture = ShowFaceEffect(currentTexture,curFaceIndex,faceStrength,faceStrengthY,facexoffset,faceyoffset,facewidth,faceheight);
                
                //扁下巴
                currentTexture = ShowFaceEffectEx(currentTexture,curFaceIndex,faceStrengthY,facexoffset,faceyoffset,facewidth,rectHeight);
            }
        }
        gl_FragColor = texture2D(inputImageTexture,currentTexture);
    }
}

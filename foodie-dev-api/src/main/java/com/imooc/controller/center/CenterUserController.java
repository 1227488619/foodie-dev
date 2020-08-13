package com.imooc.controller.center;

import com.imooc.config.CommonConfig;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.center.CenterUserBO;
import com.imooc.pojo.vo.UserVO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Jack
 * @version V1.0
 * @Package com.imooc.controller.center
 * @date 2020/7/26 17:38
 */
@Api(value = "用户信息相关", tags = {"用户信息相关API"})
@RestController
@RequestMapping("userInfo")
public class CenterUserController {

    @Autowired
    private CenterUserService centerUserService;

    @Autowired
    private FileUpload fileUpload;

    @Autowired
    private RedisOperator redisOperator;

    @ApiOperation(value = "用户头像上传", notes = "用户头像上传", httpMethod = "POST")
    @PostMapping("/uploadFace")
    public IMOOCJSONResult uploadFace (@RequestParam String userId,
                                   MultipartFile file,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        FileOutputStream fileOutputStream = null;
        String faceUrl = null;
        try {
            // 定义头像保存的位置
//            String fileSpace = CommonConfig.IMAGE_USER_FACE_LOCATION;
            String fileSpace = fileUpload.getImageUserFaceLocation();
            // 区分不同的用户
            String uploadPathPre = File.separator + userId;
            // 开始文件上传
            if (file != null) {
                // 获得文件上传的文件名称
                String fileName = file.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)){
                    String[] fileNameArr = fileName.split("\\.");
                    String suffix = fileNameArr[fileNameArr.length - 1];

                    if (!suffix.equalsIgnoreCase("png")
                            && !suffix.equalsIgnoreCase("jpg")
                            && !suffix.equalsIgnoreCase("jpeg")){
                        return IMOOCJSONResult.errorMsg("图片格式不正确");
                    }
                    String newFileName = "face-" + userId + "." +suffix;

                    // 增加时间戳保证头像能及时刷新
                    faceUrl = fileUpload.getImageServerUrl() + userId + "/" + newFileName +
                                    "?t=" + DateUtil.getCurrentDateString(DateUtil.DATE_PATTERN);

                    //上传头像最终保存的位置
                    String fileLocation =  fileSpace + uploadPathPre + File.separator + newFileName;
                    File outFile = new File(fileLocation);
                    if (outFile.getParentFile() != null) {
                        outFile.getParentFile().mkdir();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    InputStream inputStream = file.getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            } else {
                IMOOCJSONResult.errorMsg("文件不能为空");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Users user = centerUserService.updateUserFace(userId, faceUrl);

        UserVO userVO = convertUserVO(user);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userVO), true);

        return IMOOCJSONResult.ok(userVO);
    }

    @ApiOperation(value = "更新用户信息", notes = "更新用户信息", httpMethod = "POST")
    @PostMapping("/update")
    public IMOOCJSONResult update (@RequestParam String userId,
                                   @RequestBody @Valid CenterUserBO centerUserBO,
                                   BindingResult result,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        // 判断BindingResult是否保存错误的验证信息，如果有，则直接return
        if (result.hasErrors()) {
            Map<String, String> errorMap = getErrors(result);
            return IMOOCJSONResult.errorMap(errorMap);
        }

        Users user = centerUserService.updateUserInfo(userId, centerUserBO);

        UserVO userVO = convertUserVO(user);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userVO), true);

        return IMOOCJSONResult.ok(userVO);
    }

    private Map<String, String> getErrors(BindingResult result) {
        Map<String, String> map = new HashMap<>();
        List<FieldError> errorList = result.getFieldErrors();
        for (FieldError error : errorList) {
            // 发生验证错误所对应的某一个属性
            String errorField = error.getField();
            // 验证错误的信息
            String errorMsg = error.getDefaultMessage();

            map.put(errorField, errorMsg);
        }
        return map;
    }

    private UserVO convertUserVO(Users user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        // 实现用户redis会话
        String uniqueToken = UUID.randomUUID().toString().trim();
        userVO.setUniqueToken(uniqueToken);
        redisOperator.set(CommonConfig.REDIS_USER_TOKEN + ":" + user.getId(), uniqueToken);
        return userVO;
    }
}

package com.gram.gram_landlord_assistant.services.impl;

import com.gram.gram_landlord_assistant.bean.Player;
import com.gram.gram_landlord_assistant.mapper.PlayerMapper;
import com.gram.gram_landlord_assistant.server.Constant;
import com.gram.gram_landlord_assistant.server.entity.Response;
import com.gram.gram_landlord_assistant.services.PlayerInfoService;
import com.gram.gram_landlord_assistant.util.CommonUtil;
import com.gram.gram_landlord_assistant.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Slf4j
@Service
public class PlayerInfoServiceImpl implements PlayerInfoService {
    @Autowired
    private PlayerMapper playerMapper;
    private Response response = new Response();
    private final static String pictureRootPath = "D:\\landlord\\assist\\pictures";

    @Override
    public Response getPlayerInfo(String username) {
        Player player = playerMapper.selectByName(username);
        if(player == null) {
            response.setCode(Constant.ReturnCode.CODE_404);
            response.setMessage("username wrong!");
        } else {
            response.setCode(Constant.ReturnCode.CODE_200);
            response.setMessage("request OK!");
            //存入头像文件名称带后缀
            if(CommonUtil.checkNotNull(player.getAvatar())) {
                response.put("avatar", player.getAvatar().substring(player.getAvatar().lastIndexOf("/") + 1));
                response.setPiture(getPicture(player.getAvatar()));
            }
            response.put("win", player.getWin().toString());
            response.put("lose", player.getLose().toString());
            response.put("money", player.getMoney().toString());
        }
        return response;
    }

    @Override
    public Response winOnce(String username, String password, String money) {
        if(password.equals(EncryptUtil.passwordEncryptDES(playerMapper.selectPasswordByName(username)))) {
            if(playerMapper.updateWinAndScore(username, Long.parseLong(money)) == 0) {
                response.setCode(Constant.ReturnCode.CODE_500);
                response.setMessage("server error.");
            } else {
                response.setCode(Constant.ReturnCode.CODE_200);
                response.setMessage("successful");
            }
        } else {
            response.setCode(Constant.ReturnCode.CODE_403);
            response.setMessage("password wrong!!");
        }
        return response;
    }

    @Override
    public Response loseOnce(String username, String password, String money) {
        if(password.equals(EncryptUtil.passwordEncryptDES(playerMapper.selectPasswordByName(username)))) {
            if(playerMapper.updateLoseAndScore(username, Long.parseLong(money)) == 0) {
                response.setCode(Constant.ReturnCode.CODE_500);
                response.setMessage("server error.");
            } else {
                response.setCode(Constant.ReturnCode.CODE_200);
                response.setMessage("successful");
            }
        } else {
            response.setCode(Constant.ReturnCode.CODE_403);
            response.setMessage("password wrong!!");
        }
        return response;
    }

    @Override
    public Response uploadAvatar(String username, String password, String avatar, byte[] picture) {
        if(password.equals(EncryptUtil.passwordEncryptDES(playerMapper.selectPasswordByName(username)))) {
            if(picture != null && CommonUtil.checkNotNull(avatar)) {
                if(savePicture(avatar, picture) == null) {
                    response.setCode(Constant.ReturnCode.CODE_500);
                    response.setMessage("save picture error.");
                } else {
                    if(playerMapper.updateAvatar(username, savePicture(avatar, picture)) != 0){
                        response.setCode(Constant.ReturnCode.CODE_200);
                        response.setMessage("successful");
                    } else {
                        response.setCode(Constant.ReturnCode.CODE_500);
                        response.setMessage("update database error.");
                    }
                }
            }
        } else {
            response.setCode(Constant.ReturnCode.CODE_403);
            response.setMessage("password wrong!!");
        }
        return response;
    }

    /**
     * 根据路径获取图片文件的字符数组
     */
    private byte[] getPicture(String path) {
        File f;
        FileInputStream fs;
        FileChannel channel;
        try {
            f = new File(path);
            if(!f.exists()) throw new FileNotFoundException(path);
            fs = new FileInputStream(f);
            channel = fs.getChannel();
            return ByteBuffer.allocate((int)channel.size()).array();
        } catch (FileNotFoundException e) {
            log.error(path + "is not existed!");
        } catch (IOException e) {
            log.error("Error in channel IO:" + e.getMessage());
        }
        return null;
    }

    /**
     * 保存图片字符数组数据，返回路径
     * @param avatar 图片名称带后缀
     * @param p 图片文件数据
     * @return 图片在服务器中的绝对路径
     */
    private synchronized String savePicture(String avatar, byte[] p) {
        Calendar now = Calendar.getInstance();
        //创建文件夹
        String filePath = pictureRootPath + File.separator + now.get(Calendar.YEAR) +
                File.separator + (now.get(Calendar.MONTH) + 1) + File.separator + now.get(Calendar.DAY_OF_MONTH);
        File file = new File(filePath);
        if(!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if(!mkdirs) log.error("pictureFile created failed!");
        }
        //以时间戳(不要年月日)+随机数重命名图片文件
        String pictureName = new SimpleDateFormat("HHmmss").format(new Date()) +
                new Random().nextInt(100) + avatar.substring(avatar.lastIndexOf("."));
        File file1 = new File(filePath + File.separator + pictureName);
        try {
            boolean createFile = file1.createNewFile();
            if(!createFile) log.error("createFile failed!");
            FileOutputStream fos = new FileOutputStream(file1);
            fos.write(p,0,p.length);
            fos.flush();
            fos.close();
            return filePath + File.separator + pictureName;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}

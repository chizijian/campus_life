package com.dcc.app.common;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.yhx.app.campus_life.R;

import org.apache.http.HttpException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;


/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 */
public class AppException extends Exception{

	private static final long serialVersionUID = -3842263747961572087L;

	private final static boolean debug = false;//是否保存错误日志
	
	/* 异常类型 */
	public final static byte TYPE_NETWORK = 1;
	public final static byte TYPE_SOCKET = 2;
	public final static byte TYPE_HTTP_CODE = 3;
	public final static byte TYPE_HTTP_ERROR = 4;
	public final static byte TYPE_DATA_PARSER = 5;
	public final static byte TYPE_IO = 6;
	public final static byte TYPE_RUN = 7;
	
	private byte type;
	private int code;
	
	private AppException(){
	}
	
	private AppException(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;		
		if(debug){
			this.saveErrorLog(excp);
		}
	}
	
	public int getCode() {
		return this.code;
	}
	
	public int getType() {
		return this.type;
	}
	
	/**
	 * 提示友好的错误信息
	 * @param ctx
	 */
	public void makeToast(Context ctx){
		switch(this.getType()){
		case TYPE_HTTP_CODE:
			String err = ctx.getString(R.string.http_status_code_error, this.getCode());
			Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(ctx, R.string.http_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_SOCKET:
			Toast.makeText(ctx, R.string.socket_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			Toast.makeText(ctx, R.string.network_not_connected, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_DATA_PARSER:
			Toast.makeText(ctx, R.string.data_parser_failed, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_IO:
			Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_RUN:
			Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	/**
	 * 保存异常日志
	 * @param e
	 */
	@SuppressWarnings("deprecation")
	public void saveErrorLog(Exception e) {
		String errorlog = "errorlog.txt";
		String savePath = "";
		String logFilePath = "";
		PrintWriter pw = null;
		
		//判断是否挂载了SD卡
		String storageState = Environment.getExternalStorageState();		
		if(storageState.equals(Environment.MEDIA_MOUNTED)){
			savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmail/Log/";
			File file = new File(savePath);
			if(!file.exists()){
				file.mkdirs();
			}
			logFilePath = savePath + errorlog;
		}
		
		//没有挂载SD卡，无法写文件
		if(logFilePath == ""){
			return;
		}
		File logFile = new File(logFilePath);
		
		try {
			if (!logFile.exists()) {
				logFile.createNewFile();
			}
			
			pw = new PrintWriter(new FileWriter(logFile,true));//追加模式
			
			pw.println("---------"+(new Date().toLocaleString())+"-----------");	
			e.printStackTrace(pw);
		} catch (Exception ee) {
			ee.printStackTrace();		
		} finally {
			if (pw != null) {
				pw.close();
			}
		}

	}
	
	/**HTTP响应状态码异常*/
	public static AppException http(int code) {
		return new AppException(TYPE_HTTP_CODE, code, null);
	}
	
	/**HTTP异常*/
	public static AppException http(Exception e) {
		return new AppException(TYPE_HTTP_ERROR, 0 ,e);
	}

	/**网络异常*/
	public static AppException socket(Exception e) {
		return new AppException(TYPE_SOCKET, 0 ,e);
	}
	
	/**IO异常*/
	public static AppException io(Exception e) {
		if(e instanceof UnknownHostException || e instanceof ConnectException){
			return new AppException(TYPE_NETWORK, 0, e);
		}
		else if(e instanceof IOException){
			return new AppException(TYPE_IO, 0 ,e);
		}
		return run(e);
	}
	
	/** 服务器返回的数据解析异常 */
	public static AppException dataParser(Exception e) {
		return new AppException(TYPE_DATA_PARSER, 0, e);
	}
	
	/** 网络异常 */
	public static AppException network(Exception e) {
		if(e instanceof UnknownHostException || e instanceof ConnectException){
			return new AppException(TYPE_NETWORK, 0, e);
		}
		else if(e instanceof HttpException){
			return http(e);
		}
		else if(e instanceof SocketException){
			return socket(e);
		}
		return http(e);
	}
	
	/** 未知异常 */
	public static AppException run(Exception e) {
		return new AppException(TYPE_RUN, 0, e);
	}
}

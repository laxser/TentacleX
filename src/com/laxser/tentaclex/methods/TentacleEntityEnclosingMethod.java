package com.laxser.tentaclex.methods;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map.Entry;

import com.laxser.tentaclex.Method;

/**
 * @author Li Weibo (weibo.leo@gmail.com) //I believe spring-brother
 * @since 2010-3-8 下午07:15:13
 */
public abstract class TentacleEntityEnclosingMethod extends Method {

	/*public XoaEntityEnclosingMethod(String domain, String path) {
		super(domain, path);
	}*/
	
	public byte[] getBody() {
		
		//没有设置额外参数的情况
		if (this.parameters == null || this.parameters.size() == 0) {
			return new byte[0];
		}
		try {
			ByteArrayOutputStream buff = new ByteArrayOutputStream(parameters.size() * 20);
			int size = parameters.size();
			int offset = 0;
			for (Entry<String, String[]> entry : parameters.entrySet()) {
				String name = entry.getKey();
				
				String[] values = entry.getValue();
				for (int i = 0; i < values.length; i ++) {
					String value = values[i];
					
					buff.write(urlEncode(name).getBytes());
					buff.write('=');
					buff.write(urlEncode(value).getBytes());
					if (offset == size - 1 && i == values.length - 1) {	
						//最末尾的一个参数后面不加'&'
					} else {
						buff.write('&');
					}
				}
				offset ++;
			}
			return buff.toByteArray();
		} catch (IOException e) {
			//code never reach here
			e.printStackTrace();
			return new byte[]{};
		}
	}
	
}

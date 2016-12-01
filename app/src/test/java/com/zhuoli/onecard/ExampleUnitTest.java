package com.zhuoli.onecard;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

	@Test
	public void addition_isCorrect() throws Exception {

//		String hexString = "55 00 0C 4F 4B 02 00 00 BB 31 00 00 00 31 31 00 00 35 35 30 00 00 00 30 FF FF FF AA";
//
//		byte[] result = ByteString.decodeHex(hexString.replace(" ","")).toByteArray();
//		ByteString data = ByteString.of(result).substring(9,result.length-3);
//		String sn = data.substring(0,1).utf8().trim();
//		String count = data.substring(1,5).utf8().trim();
//		String bit = data.substring(5,6).utf8().trim();
//		String countMax = data.substring(6,10).utf8().trim();
//		String behindVal = data.substring(10,11).hex();
//		String countBeginVal = data.substring(11,15).utf8().trim();
//		String counterModel = data.substring(15,16).hex();
//		System.out.println(sn);
//		System.out.println(count);
//		System.out.println(bit);
//		System.out.println(countMax);
//		System.out.println(behindVal);
//		System.out.println(countBeginVal);
//		System.out.println(counterModel);

//		//10进制转16进制 -- 数值转换
//		String s = CryptoUtils.HEX.decToHexString("11",8);
//		System.out.println(s);
//
//		//16进制转10进制 -- 数值转换
//		String s1 = CryptoUtils.HEX.hexToDecString("0000000b");
//		System.out.println(s1);
		//System.out.println(ByteString.of(str2Bcd("2012")).hex());

//		Calendar ca = Calendar.getInstance();
//		ca.setTime(new Date());
//		String year = String.valueOf(ca.get(Calendar.YEAR));
//		year = year.substring(2,4);
//		year = ByteString.of(CryptoUtils.BCD.strToBcd(year)).hex();
//		String month = String.valueOf(ca.get(Calendar.MONTH)+1);
//		month = ByteString.of(CryptoUtils.BCD.strToBcd(month)).hex();
//		String day = String.valueOf(ca.get(Calendar.DAY_OF_MONTH));
//		day = ByteString.of(CryptoUtils.BCD.strToBcd(day)).hex();
//		String week = String.valueOf(ca.get(Calendar.WEEK_OF_MONTH));
//		week = ByteString.of(CryptoUtils.BCD.strToBcd(week)).hex();
//		String hour = String.valueOf(ca.get(Calendar.HOUR_OF_DAY));
//		hour = ByteString.of(CryptoUtils.BCD.strToBcd(hour)).hex();
//		String minute = String.valueOf(ca.get(Calendar.MINUTE));
//		minute = ByteString.of(CryptoUtils.BCD.strToBcd(minute)).hex();
//		String second = String.valueOf(ca.get(Calendar.SECOND));
//		second = ByteString.of(CryptoUtils.BCD.strToBcd(second)).hex();
//
//		String data = String.format("%s%s%s%s%s%s%s",year,month,day,week,hour,minute,second);
//		System.out.println(data);

//		String content = "山东假发票涉及到皮肤}1djfjwei}1}2电视剧覅偶纪委哦}2}1djfjwei}1}2电视剧覅偶纪委哦}2山东假发票涉及到皮肤}1djfjwei}1}2电视剧覅偶纪委哦}2";
//		List<PrintContent> printContentList = new ArrayList<>();
//		int start = 0;int end;
//		while (start != content.length()){
//			end = content.indexOf("}1",start);
//			if (end != -1){
//				String c = content.substring(start,end);
//				if (c.length() != 0) {
//					printContentList.add(new PrintContent(PrintContent.SINGLE,c));
//				}
//				start = end + 2;
//				end = content.indexOf("}1",start);
//				String c1 = content.substring(start,end);
//				start = end + 4;
//				end = content.indexOf("}2",start);
//				String c2 = content.substring(start,end);
//				printContentList.add(new PrintContent(PrintContent.BOTH,c1,c2));
//				start = end + 2;
//			}else {
//				printContentList.add(new PrintContent(PrintContent.SINGLE,content.substring(start,content.length())));
//				break;
//			}
//		}
//		for(PrintContent c : printContentList){
//			switch (c.getItemType()){
//				case PrintContent.SINGLE:
//					System.out.println(c.getSingleContent());
//					break;
//				case PrintContent.BOTH:
//					System.out.println(c.getSingleContent() + " " + c.getBothContent());
//					break;
//			}
//		}

//		System.out.println(Pattern.compile("(?i)^ZeroInk|Octopus").matcher("ZerOINk").find());
	}
}
package tests.com.lectek;

import junit.framework.TestCase;

public class FormatPluginTest extends TestCase {

    public void testFormatPlugin()throws Exception {
		// 1.由文件后缀名来选择插件
//		FormatPlugin fp = PluginManager.instance()
//				.getPlugin("/sdcard/1.ceb");
//		//System.out.println("书的目录长度:" + fp.getTocItemNavLabels().length);
//		// 2.通过插件读取书的元数据
//		BookMetaInfo bmi = fp.getBookMetaInfo();
//		System.out.println("bookID:" + bmi.getBookId());
//		System.out.println("bookName:" + bmi.getBookTitle());
//		System.out.println("author:" + bmi.getAuthor());
//		// 3.通过插件读取书的实体内容
//		List<TOCItem> myList = fp.getTocItems();
//		System.out.println("chapterCount:[" + myList.size() + "]");
//		for (int i = 0; i < myList.size(); i++) {
//			TOCItem tocItem = (TOCItem) myList.get(i);
//			System.out.println(tocItem.navLabel + "：" + tocItem.contentSrc);
//		}
//		if(bmi.type.equals(BookMetaInfo.BOOK_META_INFO_FORMAT)){//版式
//			Smiler smiler = fp.getChapterSmilPage(0);
//			System.out.println(smiler.toString());
//		}else if(bmi.type.equals(BookMetaInfo.BOOK_META_INFO_STREAM)){//流式
//			String content = fp.getChapterHtmlText(0);
//			System.out.println(content);
//		}
    }
}

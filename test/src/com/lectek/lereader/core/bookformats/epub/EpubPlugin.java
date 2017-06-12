package com.lectek.lereader.core.bookformats.epub;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.text.TextUtils;

import com.lectek.lereader.core.bookformats.BookInfo;
import com.lectek.lereader.core.bookformats.Catalog;
import com.lectek.lereader.core.bookformats.Chapter;
import com.lectek.lereader.core.bookformats.FormatPlugin;
import com.lectek.lereader.core.bookformats.NoSupportBookFormatException;
import com.lectek.lereader.core.bookformats.epub.paser.ContainerHandler;
import com.lectek.lereader.core.bookformats.epub.paser.NAVHandler;
import com.lectek.lereader.core.bookformats.epub.paser.NCXHandler;
import com.lectek.lereader.core.bookformats.epub.paser.OPFHandler;
import com.lectek.lereader.core.bookformats.util.IOUtil;
import com.lectek.lereader.core.bookformats.util.XMLUtil;
import com.lectek.lereader.core.util.EncryptUtils;
import com.lectek.lereader.core.util.LogUtil;

/** EPUB格式书籍的解析
 * @author mingkg21
 * @email mingkg21@gmail.com
 * @date 2013-2-19
 */
public class EpubPlugin extends FormatPlugin {
	
	private static final String DEFAULT_OPF_FILE_LOCATION = "OEBPS/content.opf";
	private static final String CONTAINER_FILE_LOCATION = "META-INF/container.xml";
	private String secretKey;
	private String opfFilePath;
	private HashMap<String, ManifestResource> manifestIdResources;
	private HashMap<String, ManifestResource> manifestHrefResources;
	private HashMap<String,Catalog> catalogHrefMap;
	private HashMap<String, Resource> allResources = new HashMap<String, Resource>();

	public EpubPlugin(String filePath) {
		super(filePath);
		setSystemFormat(true);
	}

	@Override
	public void init(String secretKey) throws Exception {
		boolean hadContainerfile = false;
		this.secretKey = secretKey;
		ZipFile zipFile = new ZipFile(filePath);
		ZipEntry zipEntry = null;
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		if(enumeration != null){
			for (;enumeration.hasMoreElements();) {
				zipEntry = enumeration.nextElement();
				String name = zipEntry.getName();
				LogUtil.i("zip entry name: ", zipEntry.getName());
				if(zipEntry.isDirectory()){
					continue;
				}
				allResources.put(name, new Resource(filePath, name));
				if(CONTAINER_FILE_LOCATION.equals(name)){//解析container.xml文件获取OPF文件路径
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					byte[] data = IOUtil.toByteArray(inputStream);
					inputStream.close();
					ContainerHandler handler = new ContainerHandler();
					if(XMLUtil.parserXml(handler, data)){
						opfFilePath = handler.getOpfFilePath();
						hadContainerfile = true;
						LogUtil.i("opf file path: ", opfFilePath);
					}
				}
				if(opfFilePath != null && opfFilePath.equals(name)){//查找OFP文件，并解析
					InputStream inputStream = zipFile.getInputStream(zipEntry);
					initBookInfo(inputStream);
					inputStream.close();
				}
			}
		}
		zipFile.close();
		if(bookInfo == null){//如果之前没解析到，重新解析获取书籍信息
			if(opfFilePath == null){
				opfFilePath = DEFAULT_OPF_FILE_LOCATION;
			}
			zipFile = new ZipFile(filePath);
			enumeration = zipFile.entries();
			if(enumeration != null){
				for (;enumeration.hasMoreElements();) {
					zipEntry = enumeration.nextElement();
					String name = zipEntry.getName();
					LogUtil.i("zip entry name: ", zipEntry.getName());
					if(zipEntry.isDirectory()){
						continue;
					}
					if(opfFilePath != null && opfFilePath.equals(name)){//查找OFP文件，并解析
						initBookInfo(zipFile.getInputStream(zipEntry));
					}
				}
			}
			zipFile.close();
		}
		if(!hadContainerfile){//没找到META-INF/container.xml文件，则认为不是正确的EPUB格式书籍
			throw new NoSupportBookFormatException(filePath + " isn't a epub book file!");
		}
	}
	
	/** 初始化书籍信息
	 * @param zis
	 * @throws IOException
	 */
	private void initBookInfo(InputStream zis) throws IOException {
		byte[] data = IOUtil.toByteArray(zis);
		OPFHandler handler = new OPFHandler(filePath);
		if(XMLUtil.parserXml(handler, data)){
			//解析书籍信息
			bookInfo = handler.getBookInfo();
			//获取目录位置
			setChapterIds(handler.getChapterIds());
			//缓存资源文件位置
			manifestIdResources = handler.getIdResources();
			manifestHrefResources = handler.getHrefResources();
			contentCover = handler.contentCover;
			String navFilePath = handler.getNavFilePath();
			String ncxId = handler.getNcxId();
			//解析目录
			if(manifestIdResources != null){
				if(!TextUtils.isEmpty(navFilePath)){
					Resource resource = manifestHrefResources.get(navFilePath);
					if(resource != null){
						NAVHandler navHandler = new NAVHandler(navFilePath);
						byte[] navData = resource.getData();
						if (!TextUtils.isEmpty(secretKey)) {
							navData = EncryptUtils.decryptByAES(navData, secretKey);
						}
						if(XMLUtil.parserXml(navHandler, navData)){
							ArrayList<Catalog> catalogs = navHandler.getCatalogs();
							catalogHrefMap = navHandler.getCatalogHrefMap();
							setCatalog(catalogs);
						}
					}
				}
				if(getCatalog().size() == 0 && !TextUtils.isEmpty(ncxId)){
					Resource resource = manifestIdResources.get(ncxId);
					if(resource != null){
						NCXHandler ncxHandler = new NCXHandler();
						byte[] ncxData = resource.getData();
						if (!TextUtils.isEmpty(secretKey)) {
							ncxData = EncryptUtils.decryptByAES(ncxData, secretKey);
						}
						if(XMLUtil.parserXml(ncxHandler, ncxData)){
							ArrayList<Catalog> catalogs = ncxHandler.getCatalogs();
							catalogHrefMap = ncxHandler.getCatalogHrefMap();
							Collections.sort(catalogs, new CatalogComparator());
							setCatalog(catalogs);
							ArrayList<String> chapterIds = new ArrayList<String>();
							for (Catalog catalog : catalogs) {
								ManifestResource resources = manifestHrefResources.get(catalog.getHref());
								if(resources != null && !TextUtils.isEmpty(resources.getId())){
									chapterIds.add(resources.getId());
								}
							}
							setChapterIds(chapterIds);
						}
					}
				}
			}
			LogUtil.i("book info: ", bookInfo.toString());
		}
	}
	
	private class CatalogComparator implements Comparator<Catalog> {
		@Override
		public int compare(Catalog object1, Catalog object2) {
			return object1.getIndex() - object2.getIndex();
		}
	}
		
	@Override
	protected void setFormat(String format) {
		
	}
	
	@Override
	public void recyle() {
		
	}
	@Override
	public Catalog getCatalogByIndex(int index) {
		String chapterID = getChapterIds().get(index);
		Resource resource = manifestIdResources.get(chapterID);
		if(resource != null && catalogHrefMap != null){
			return catalogHrefMap.get(resource.getHref());
		}
		return null;
	}
	
	@Override
	public int getChapterIndex(Catalog catalog) {
		ManifestResource resource = manifestHrefResources.get(catalog.getHref());
		if(resource != null){
			ArrayList<String> chapterIds = getChapterIds();
			for (int i = 0;i < chapterIds.size();i++) {
				String id = chapterIds.get(i);
				if(id.equals(resource.getId())){
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public Chapter getChapter(String chapterID) throws Exception {
		Resource resource = manifestIdResources.get(chapterID);
		if(resource != null && catalogHrefMap != null){
			String title = null;
			Catalog catalog = catalogHrefMap.get(resource.getHref());
			if(catalog != null){
				title = catalog.getText();
			}
			Chapter chapter = new Chapter(chapterID, title, resource.getData());
			return chapter;
		}
		return null;
	}
	
	@Override
	public Resource findResource(String path){
		Resource resource = null;
		if(!TextUtils.isEmpty(path)){
			if(manifestHrefResources != null){
				resource = manifestHrefResources.get(path);
				if(resource == null){//校验是否获取到资源，如果无法获取到资源，重新遍历资源MAP
					Set<String> keys = manifestHrefResources.keySet();
					for(String key : keys){
						if(key.endsWith(path) || path.endsWith(key)){
							resource = manifestHrefResources.get(key);
							break;
						}
					}
				}
				if(resource == null){
					Set<String> keys = allResources.keySet();
					for(String key : keys){
						if(key.endsWith(path) || path.endsWith(key)){
							resource = allResources.get(key);
							break;
						}
					}
				}
			}
		}
		return resource;
	}
	@Override
	public BookInfo getEpubBookInfo() throws Exception {
		//解析获取书籍信息
		opfFilePath = DEFAULT_OPF_FILE_LOCATION;
		BookInfo bookInfo = null;
		ZipEntry zipEntry = null;
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(filePath);
			Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
			if(enumeration != null){
				for (;enumeration.hasMoreElements();) {
					zipEntry = enumeration.nextElement();
					String name = zipEntry.getName();
					LogUtil.i("zip entry name: ", zipEntry.getName());
					if(zipEntry.isDirectory()){
						continue;
					}
					if(opfFilePath != null && opfFilePath.equals(name)){//查找OFP文件，并解析
						byte[] data = IOUtil.toByteArray(zipFile.getInputStream(zipEntry));
						OPFHandler handler = new OPFHandler(filePath);
						if(XMLUtil.parserXml(handler, data)){
							//解析书籍信息
							bookInfo = handler.getBookInfo();
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			if (zipFile!=null) {
				zipFile.close();
			}
		}
		return bookInfo;
	}
	
	public String contentCover;
    // 声明图片后缀名数组
    private final String IMAGE [] = {"jpg","bmp","png","jpeg","gif","jpe","ico"};
    private final String COVER_NAME = "Cover";//乐阅epub标准:封面图片资源id固定为cover.*（蔡文斌提供）
    @Override
    public InputStream getCoverStream(){
    	if (TextUtils.isEmpty(contentCover)) {
            for (String image: IMAGE){
                String coverName = COVER_NAME +"."+ image;
                if(manifestIdResources.containsKey(coverName)){
                    Resource resource = manifestIdResources.get(coverName);
                    try {
                        return resource.getDataStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
		}else{  
            if(manifestIdResources.containsKey(contentCover)){
                Resource resource = manifestIdResources.get(contentCover);
                try {
                    return resource.getDataStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
        return null;
    }
}

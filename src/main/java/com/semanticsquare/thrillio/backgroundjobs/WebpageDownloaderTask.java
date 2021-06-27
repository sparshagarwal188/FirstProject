package com.semanticsquare.thrillio.backgroundjobs;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.semanticsquare.thrillio.entities.WebLink;
import com.semanticsquare.thrillio.util.HttpConnect;
import com.semanticsquare.thrillio.util.IOUtil;
import com.semanticsquare.thrillio.dao.BookmarkDao;

public class WebpageDownloaderTask implements Runnable{
	private boolean downloadAll;
	private static BookmarkDao dao = new BookmarkDao();
	private static final long TIME_FRAME = 3000000000L;
	
	ExecutorService downloader = Executors.newFixedThreadPool(5);
	
	public WebpageDownloaderTask(boolean downloadAll) {
		this.downloadAll = downloadAll;
	}
	
	public static class Downloader<V extends WebLink> implements Callable<V>{
		private V weblink;
		
		public Downloader(V weblink) {
			this.weblink = weblink;
		}
		public V call() {
			try {
				if (!weblink.getUrl().endsWith(".pdf")) {
					weblink.setDownloadStatus(WebLink.DownloadStatus.FAILED);
					String html = HttpConnect.download(weblink.getUrl());
					weblink.setHtmlPage(html);
				} else {
					weblink.setDownloadStatus(WebLink.DownloadStatus.NOT_ELIGIBLE);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			return weblink;
		}
	}
	
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			List<WebLink> weblinks = getWeblinks();
			
			if (weblinks.size() > 0) {
				download(weblinks);
			} else {
				System.out.println("No new weblinks to download");
			}
			
			try {
				TimeUnit.SECONDS.sleep(15);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		downloader.shutdown(); 
	}
	
	private List<WebLink> getWeblinks() {
		List<WebLink> result = null;
		if (downloadAll) {
			result = dao.getAllWeblinks();
			downloadAll = false;
		} else {
			result = dao.getWeblinks(WebLink.DownloadStatus.NOT_ATTEMPTED);
		}
		
		return result;
	}
	
	private void download(List<WebLink> weblinks) {
		List<Downloader<WebLink>> tasks = new ArrayList<>();
		List<Future<WebLink>> futures = new ArrayList<>();
		
		for (WebLink weblink : weblinks) {
			tasks.add(new Downloader(weblink));
		}
		
		try {
			futures = downloader.invokeAll(tasks, TIME_FRAME, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		for (Future<WebLink> future : futures) {
			try {
				if (!future.isCancelled()) {
					WebLink weblink = future.get();
					String webPage = weblink.getHtmlPage();
					if (webPage != null) {
						IOUtil.write(webPage,  weblink.getId());
						weblink.setDownloadStatus(WebLink.DownloadStatus.SUCCESS);
						System.out.println("Webpage downloaded : " + weblink.getUrl());
					} else {
						System.out.println("Webpage not downloaded : " + weblink.getUrl());
					}
				} else {
					System.out.println("\n Thread was cancelled");
				}
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

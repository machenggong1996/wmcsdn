package csdnblog;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * CSDN博客爬虫
 *
 * @author steven
 * @describe 可以爬取指定用户的csdn博客所有文章，并保存到数据库中。
 * @date 2016-4-30
 * @csdn qq598535550
 * @website lyf.soecode.com
 */
public class CsdnBlogPageProcessor implements PageProcessor {

    private static String username = "qq598535550";// 设置csdn用户名
    private static int size = 0;// 共抓取到的文章数量

    // 抓取网站的相关配置，包括：编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    // process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {
        // 列表页
        if (!page.getUrl().regex("https://blog\\.csdn\\.net/" + username + "/article/details/\\d+").match()) {
            // 添加所有文章页
            page.addTargetRequests(page.getHtml().xpath("//div[@class='article-list']").links()// 限定文章列表获取区域
                    .regex("/" + username + "/article/details/\\d+")
                    .replace("/" + username + "/", "https://blog.csdn.net/" + username + "/")// 巧用替换给把相对url转换成绝对url
                    .all());
            // 添加其他列表页
            page.addTargetRequests(page.getHtml().xpath("//div[@class='article-item-box csdn-tracking-statistics']").links()// 限定其他列表页获取区域
                    .regex("/" + username + "/article/list/\\d+")
                    .replace("/" + username + "/", "https://blog.csdn.net/" + username + "/")// 巧用替换给把相对url转换成绝对url
                    .all());
            // 文章页
        } else {
            size++;// 文章数量加1
            // 用CsdnBlog类来存抓取到的数据，方便存入数据库
            CsdnBlog csdnBlog = new CsdnBlog();
            // 设置编号
            csdnBlog.setId(Integer.parseInt(
                    page.getUrl().regex("https://blog\\.csdn\\.net/" + username + "/article/details/(\\d+)").get()));
            // 设置标题
            csdnBlog.setTitle(
                    page.getHtml().xpath("//div[@class='article-title-box']/h1[@class='title-article']/text()").get());
            // 设置日期
            csdnBlog.setDate(
                    page.getHtml().xpath("//div[@class='article-info-box']/div[@class='article-bar-top d-flex']/span[@class='time']/text()").get());
            // 设置标签（可以有多个，用,来分割）
            csdnBlog.setTags(listToString(page.getHtml()
                    .xpath("//div[@class='markdown_views']/h3/allText()").all()));
            // 设置类别（可以有多个，用,来分割）
            csdnBlog.setCategory(
                    listToString(page.getHtml().xpath("//div[@class='markdown_views']/p/allText()").all()));
            // 设置阅读人数
            csdnBlog.setView(page.getHtml().xpath("//div[@class='article-bar-top d-flex']/div[@class='float-right']/span[@class='read-count']/text()").get());
            // 设置评论人数
            csdnBlog.setComments(Integer.parseInt(page.getHtml()
                    .xpath("//div[@class='data-info d-flex item-tiling']/dl/dd/span[@class='count']/text()").get()));
            // 设置是否原创
            csdnBlog.setCopyright(page.getHtml().regex("article-copyright").match() ? 1 : 0);
            // 把对象存入数据库
            new CsdnBlogDao().add(csdnBlog);
            // 把对象输出控制台
            System.out.println(csdnBlog);
        }
    }

    // 把list转换为string，用,分割
    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }

    public static void main(String[] args) {
        long startTime, endTime;
        System.out.println("【爬虫开始】请耐心等待一大波数据到你碗里来...");
        startTime = System.currentTimeMillis();
        // 从用户博客首页开始抓，开启5个线程，启动爬虫
        Spider.create(new CsdnBlogPageProcessor()).addUrl("http://blog.csdn.net/" + username).thread(4).run();
        endTime = System.currentTimeMillis();
        System.out.println("【爬虫结束】共抓取" + size + "篇文章，耗时约" + ((endTime - startTime) / 1000) + "秒，已保存到数据库，请查收！");
    }
}

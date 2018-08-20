package csdnblog;

import java.sql.*;

public class CsdnBlogDao {

	private Connection conn = null;
	private Statement stmt = null;

	public CsdnBlogDao() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost:3306/webmagic?user=root&password=199601300837Mm";
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webmagic","root","199601300837Mm");
			stmt = conn.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public int add(CsdnBlog csdnBlog) {
		try {
			String sql = "INSERT INTO `webmagic`.`csdnblog` (`id`, `title`, `date`, `tags`, `category`, `view`, `comments`, `copyright`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, csdnBlog.getId());
			ps.setString(2, csdnBlog.getTitle());
			ps.setString(3, csdnBlog.getDate());
			ps.setString(4, csdnBlog.getTags());
			ps.setString(5, csdnBlog.getCategory());
			ps.setString(6, csdnBlog.getView());
			ps.setInt(7, csdnBlog.getComments());
			ps.setInt(8, csdnBlog.getCopyright());
			System.out.println(ps);
			return ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

}

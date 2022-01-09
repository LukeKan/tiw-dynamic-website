package it.polimi.tiw.plain_html.controllers.auction;

import com.google.gson.Gson;
import it.polimi.tiw.plain_html.beans.AuctionBean;
import it.polimi.tiw.plain_html.beans.BidBean;
import it.polimi.tiw.plain_html.dao.AuctionDAO;
import it.polimi.tiw.plain_html.dao.BidDao;
import it.polimi.tiw.plain_html.utils.ConnectionHandler;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@WebServlet("/auction/details")
public class DetailsAuction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public DetailsAuction() {
        super();
    }

    public void init() throws ServletException {
        connection = ConnectionHandler.getConnection(getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        BidDao bidDao = new BidDao(connection);
        try {
            Integer auctionID = Integer.parseInt(request.getParameter("id"));

            AuctionBean auction = auctionDAO.getAuction(auctionID);
            auction.setMaxBid(bidDao.getMaxBidFromAuctionID(auctionID));

            List<BidBean> bids = bidDao.getBidsOfAuction(auctionID);
            AtomicReference<BidBean> winnerBid=new AtomicReference<>();
            winnerBid.set(null);
            if (auction.getClosed()){
                Optional<BidBean> winner = bids.stream()
                        .max(BidBean::compareTo);
                winner.ifPresent(winnerBid::set);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            StringBuilder json = new StringBuilder();
            json.append("{" +
                    "\"auction\":");
            json.append(new Gson().toJson(auction));
            json.append(", \"bids\":[");
            for (BidBean bid : bids) {
                json.append(new Gson().toJson(bid));
                json.append(",");
            }
            if (!bids.isEmpty())json.deleteCharAt( json.length() - 1 );
            json.append("], \"winnerBid\":");
            if (winnerBid.get()!=null){
                json.append(new Gson().toJson(winnerBid.get()));
            } else {
                json.append("{}");
            }
            json.append("}");
            response.getWriter().println(json);
        } catch (SQLException throwables) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("ERROR: "+throwables.getMessage());
        }
    }

    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}

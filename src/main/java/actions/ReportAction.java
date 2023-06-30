package actions;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.ServletException;

import actions.views.EmployeeView;
import actions.views.FollowView;
import actions.views.GoodView;
import actions.views.ReportView;
import constants.AttributeConst;
import constants.ForwardConst;
import constants.JpaConst;
import constants.MessageConst;
import services.FollowService;
import services.GoodService;
import services.ReportService;

/**
 * 日報に関する処理を行うクラス
 *
 */

public class ReportAction extends ActionBase {

    private ReportService service;

    /**
     * フロントコントローラから直接呼び出されるメソッド
     */
    @Override
    public void process() throws ServletException, IOException{

        service = new ReportService();

        //メソッドを実行
        invoke();
        service.close();

    }

    /**
     * 一覧画面を表示する
     * @throws ServletException
     * @throws IOException
     */
    public void index() throws ServletException, IOException{

        //指定されたページ数の一覧画面に表示する日報データを取得
        int page = getPage();
        List<ReportView> reports = service.getAllPerPage(page);

        //全日報データの件数を取得
        long reportsCount = service.countAll();

        putRequestScope(AttributeConst.REPORTS, reports); //取得した日報データ
        putRequestScope(AttributeConst.REP_COUNT, reportsCount); //全ての日報データの件数
        putRequestScope(AttributeConst.PAGE, page); //ページ数
        putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数

        //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
        String flush = getSessionScope(AttributeConst.FLUSH);
        if(flush != null) {
            putRequestScope(AttributeConst.FLUSH,flush);
            removeSessionScope(AttributeConst.FLUSH);
        }

        //一覧画面を表示
        forward(ForwardConst.FW_REP_INDEX);

    }

    /**
     * 新規登録画面を表示する
     * @throws ServletException
     * @throws IOException
     */

    public void entryNew() throws ServletException, IOException{

        putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン

        //日報情報の空インスタンスに、日報の日付＝今日の日付を設定する
        ReportView rv = new ReportView();
        rv.setReportDate(LocalDate.now());
        putRequestScope(AttributeConst.REPORT, rv);//日付のみ設定済みの日報インスタンス

        //新規登録画面を表示
        forward(ForwardConst.FW_REP_NEW);

    }
    /**
     * 新規登録を行う
     * @throws ServletException
     * @throws IOException
     */
    public void create() throws ServletException, IOException {

        //CSRF対策 tokenのチェック
        if (checkToken()) {

            //日報の日付が入力されていなければ、今日の日付を設定
            LocalDate day = null;
            if (getRequestParam(AttributeConst.REP_DATE) == null
                    || getRequestParam(AttributeConst.REP_DATE).equals("")) {
                day = LocalDate.now();
            } else {
                //getRequestParam()でString型で受け取った日付を LocalDate 型にキャスト
                day = LocalDate.parse(getRequestParam(AttributeConst.REP_DATE));
            }

            //セッションからログイン中の従業員情報を取得
            EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

            //パラメータの値をもとに日報情報のインスタンスを作成する
            ReportView rv = new ReportView(
                    null,
                    ev, //ログインしている従業員を、日報作成者として登録する
                    day,
                    getRequestParam(AttributeConst.REP_TITLE),
                    getRequestParam(AttributeConst.REP_CONTENT),
                    null,
                    null,
                    0);

            //日報情報登録
            List<String> errors = service.create(rv);

            if (errors.size() > 0) {
                //登録中にエラーがあった場合

                putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                putRequestScope(AttributeConst.REPORT, rv);//入力された日報情報
                putRequestScope(AttributeConst.ERR, errors);//エラーのリスト

                //新規登録画面を再表示
                forward(ForwardConst.FW_REP_NEW);

            } else {
                //登録中にエラーがなかった場合

                //セッションに登録完了のフラッシュメッセージを設定
                putSessionScope(AttributeConst.FLUSH, MessageConst.I_REGISTERED.getMessage());

                //一覧画面にリダイレクト
                redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
            }
        }
    }



    /**
     * 詳細画面を表示する
     * @throws ServletException
     * @throws IOException
     */
     public void show() throws ServletException, IOException {

         //idを条件に日報データを取得する
         ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));
         EmployeeView ev1 = rv.getEmployee();

         //セッションからログイン中の従業員情報を取得
         EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);


        //指定の日報にいいねした従業員の件数を取得
         GoodService goodService = new GoodService();
         long getGoodCount = goodService.getGoodCount(ev, rv);

         //ログイン中の従業員がフォローした日報の件数を取得
         FollowService fs = new FollowService();
         long myFollowsCount = fs.followCount(ev1, ev);



         if(rv == null) {
             //該当の日報データが存在しない場合はエラー画面を表示
             forward(ForwardConst.FW_ERR_UNKNOWN);

         }else {
             putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ
             putRequestScope(AttributeConst.LOGIN_EMP, ev); //ログイン中の従業員データ

             request.setAttribute("follow_count",myFollowsCount );
             request.setAttribute("good_count", getGoodCount);//指定の日報にいいねした従業員の件数を取得

             //詳細画面を表示
             forward(ForwardConst.FW_REP_SHOW);
         }
     }

     /**
      * 編集画面を表示する
      * @throws ServletException
      * @throws IOException
      */
     public void edit() throws ServletException, IOException{

     //idを条件に日報データを取得する
     ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

     //セッションからログイン中の従業員情報を取得
     EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

     if(rv == null || ev.getId() != rv.getEmployee().getId()) {
         //該当の日報データが存在しない、または
         //ログインしている従業員が日報の作成者でない場合はエラー画面を表示
         forward(ForwardConst.FW_ERR_UNKNOWN);

     }else {
         putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
         putRequestScope(AttributeConst.REPORT,rv); //取得した日報データ

         //編集画面を表示
         forward(ForwardConst.FW_REP_EDIT);
     }

    }

     /**
      * 更新処理を行う
      * @throws ServletException
      * @throws IOException
      */
     public void update() throws ServletException, IOException{

         //CSRF対策tokenのチェック
         if(checkToken()) {

             //idを条件に日報データを取得する
             ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

             //入力された日報内容を設定する
             rv.setReportDate(toLocalDate(getRequestParam(AttributeConst.REP_DATE)));
             rv.setTitle(getRequestParam(AttributeConst.REP_TITLE));
             rv.setContent(getRequestParam(AttributeConst.REP_CONTENT));

             //日報データを更新する
             List<String> errors = service.update(rv);

             //更新中にエラーが発生した場合
             if(errors.size() > 0) {
                 putRequestScope(AttributeConst.TOKEN, getTokenId()); //CSRF対策用トークン
                 putRequestScope(AttributeConst.REPORT, rv); //入力された日報情報
                 putRequestScope(AttributeConst.ERR, errors); //エラーのリスト

                 //編集画面を再表示
                 forward(ForwardConst.FW_REP_EDIT);
             }else {
                 //更新中にエラーがなかった場合

                 //セッションに更新完了のフラッシュメッセージを設定
                 putSessionScope(AttributeConst.FLUSH, MessageConst.I_UPDATED.getMessage());

                 //一覧画面にリダイレクト
                 redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);

             }

         }

     }


     /**
      * 日報にいいねするメソッド
      */
     public void good() throws ServletException, IOException{


         //idを条件に日報データを取得する
         ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

         //ログイン中の従業員情報を取得
         EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

         //現在日時を取得
         LocalDateTime day = LocalDateTime.now();

         //パラメータの値をもとにいいね情報のインスタンスを作成する
         GoodView gv = new GoodView(
                 null,
                 ev, //ログインしている従業員を、いいねする者として登録する
                 rv, //いいねをする日報
                 day, //いいねする日時
                 day);


         //いいね数を加算する
         int goodCount = rv.getGoodCount();//ReportViewクラスのgetGoodCount()を呼び出し、変数goodCount代入(現在のいいね数)。
         goodCount = goodCount + 1;//現在のいいね数に1加算する。
         rv.setGoodCount(goodCount);//rv.setGoodCountメソッドで加算したいいね数をセットする。


         //いいねテーブルに登録する
         GoodService goodService = new GoodService();//GoodServiceクラス呼び出し
         goodService.create(gv);//createメソッドでテーブルに登録

         //日報データを更新する
         service.updated(rv);


         //セッションにいいね完了のフラッシュメッセージをセット
         putSessionScope(AttributeConst.FLUSH, MessageConst.I_GOOD_COUNTED.getMessage());

         //一覧画面にリダイレクト
         redirect(ForwardConst.ACT_REP, ForwardConst.CMD_INDEX);
         }

         /**
          * いいねした人一覧ページを表示する
          * @throws ServletException
          * @throws IOException
          */
         public void goodIndex() throws ServletException, IOException{


             GoodService goodService = new GoodService();

             //idを条件に日報データを取得する
             ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

             //指定した日報のいいねデータを、指定されたページ数の一覧画面に表示する分取得する
            int page = getPage();
             List<GoodView> goods = goodService.getMinePerPage(rv, page);


             //指定した日報データのいいね件数を取得
             long myGoodsCount = goodService.countAllMine(rv);

             //jspに送る値をセット
             request.setAttribute("goods",goods);//取得した日報データ
             request.setAttribute("goods_count", myGoodsCount); //指定した日報のいいねの数
             putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ
             putRequestScope(AttributeConst.PAGE, page); //ページ数
             putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数


             //いいねした人一覧画面を表示
             forward(ForwardConst.FW_REP_GOOD);

             }


         /**
          * 日報の作成者をフォローするメソッド
          */
         public void follow() throws ServletException, IOException{


             //idを条件に日報データを取得する
             ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));

             EmployeeView ev1 = rv.getEmployee();

             //ログイン中の従業員情報を取得
             EmployeeView ev = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

             //現在日時を取得
             LocalDateTime day = LocalDateTime.now();

             //パラメータの値をもとにいいね情報のインスタンスを作成する
             FollowView fv = new FollowView(
                     null,
                     ev, //ログインしている従業員を、フォローする者として登録する
                     ev1, //日報の作成者をフォローされる者として登録する
                     day, //フォローする日時
                     day);

             //フォローテーブルに登録する
             FollowService fs = new FollowService();//GoodServiceクラス呼び出し
             fs.create(fv);//createメソッドでテーブルに登録


             //セッションにフォロー完了のフラッシュメッセージをセット
             putSessionScope(AttributeConst.FLUSH, MessageConst.I_FOLLOWED.getMessage());

             //タイムライン画面にリダイレクト
             redirect(ForwardConst.ACT_REP, ForwardConst.CMD_FOLLOW_INDEX);
             }





         /**
          * タイムラインページを表示する
          * @throws ServletException
          * @throws IOException
          */
         public void followIndex() throws ServletException, IOException{

             //セッションからログイン中の従業員情報を取得
             EmployeeView loginEmployee = (EmployeeView) getSessionScope(AttributeConst.LOGIN_EMP);

             //idを条件に日報データを取得する
             ReportView rv = service.findOne(toNumber(getRequestParam(AttributeConst.REP_ID)));


             //ログイン中の従業員がフォローした従業員の日報データを、指定されたページ数の一覧画面に表示する分取得する
             int page = getPage();
             FollowService fs = new FollowService();
             List<ReportView> reports = fs.getMinePerPage(loginEmployee, page);

             //ログイン中の従業員がフォローした日報の件数を取得
             long myFollowsCount = fs.getFollowCount(loginEmployee);


             //JSPに送る値をセットする
             request.setAttribute("reports", reports); //取得した日報データ
             putRequestScope(AttributeConst.REPORT, rv); //取得した日報データ
             request.setAttribute("follow_count",myFollowsCount);
             putRequestScope(AttributeConst.PAGE, page); //ページ数
             putRequestScope(AttributeConst.MAX_ROW, JpaConst.ROW_PER_PAGE); //1ページに表示するレコードの数


             //セッションにフラッシュメッセージが設定されている場合はリクエストスコープに移し替え、セッションからは削除する
             String flush = getSessionScope(AttributeConst.FLUSH);
             if (flush != null) {
                 putRequestScope(AttributeConst.FLUSH, flush);
                 removeSessionScope(AttributeConst.FLUSH);
             }

             //タイムライン画面を表示
             forward(ForwardConst.FW_REP_FOLLOW);

             }
}
























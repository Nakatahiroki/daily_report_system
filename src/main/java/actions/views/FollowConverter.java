package actions.views;

import java.util.ArrayList;
import java.util.List;

import models.Follow;

/**
 * フォローデータのDTOモデル⇔Viewモデルの変換を行うクラス
 *
 */
public class FollowConverter {

    /**
     * ViewモデルのインスタンスからDTOモデルのインスタンスを生成する
     * @param fv FollowViewのインスタンス
     * @return Followのインスタンス
     *
     */
    public static Follow toModel(FollowView fv) {
        return new Follow(
                fv.getId(),
                EmployeeConverter.toModel(fv.getEmployee()), //フォローした従業員
                EmployeeConverter.toModel(fv.getEmployeed()), //フォローされた従業員
                fv.getCreatedAt(),
                fv.getUpdatedAt());
    }
    /**
     * DTOモデルのインスタンスからViewモデルのインスタンスを作成する
     * @param f Followのインスタンス
     * @return FollowViewのインスタンス
     */
    public static FollowView toView(Follow f) {
        if(f == null) {
            return null;
        }
        return new FollowView(
                f.getId(),
                EmployeeConverter.toView(f.getFollow_employee_id()),
                EmployeeConverter.toView(f.getFollowed_employee_id()),
                f.getCreatedAt(),
                f.getUpdatedAt());
    }

    /**
     * DTOモデルのリストからViewモデルのリストを生成する
     * @param list DTOモデルのリスト
     * @return Viewモデルのリスト
     */
    public static List<FollowView> toViewList(List<Follow> list){
        List<FollowView> fvs = new ArrayList<>();

        for(Follow f : list) {
            fvs.add(toView(f));
        }
        return fvs;
    }

    /**
     * Viewモデルの全フィールドの内容をDTOモデルのフィールドにコピーする
     * @param f DTOモデル（コピー先）
     * @param fv Viewモデル（コピー元）
     */
    public static void copyViewToModel(Follow f, FollowView fv) {
        f.setId(fv.getId());
        f.setFollow_employee_id(EmployeeConverter.toModel(fv.getEmployee()));
        f.setFollowed_employee_id(EmployeeConverter.toModel(fv.getEmployeed()));
        f.setCreatedAt(fv.getCreatedAt());
        f.setUpdatedAt(fv.getUpdatedAt());
    }


}

package cn.cnic.component.livy.mapper;

import cn.cnic.component.livy.entity.CodeSnippet;
import cn.cnic.component.livy.mapper.provider.CodeSnippetMapperProvider;
import cn.cnic.component.livy.vo.CodeSnippetVo;
import java.util.List;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;
import org.apache.ibatis.mapping.FetchType;

@Mapper
public interface CodeSnippetMapper {

  /**
   * add CodeSnippet
   *
   * @param codeSnippet codeSnippet
   * @return Integer
   */
  @InsertProvider(type = CodeSnippetMapperProvider.class, method = "addCodeSnippet")
  Integer addCodeSnippet(CodeSnippet codeSnippet);

  /**
   * update CodeSnippet
   *
   * @param codeSnippet codeSnippet
   * @return Integer
   */
  @UpdateProvider(type = CodeSnippetMapperProvider.class, method = "updateCodeSnippet")
  Integer updateCodeSnippet(CodeSnippet codeSnippet);

  /**
   * update CodeSnippet enable_flag
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param id id
   * @return Integer
   */
  @UpdateProvider(type = CodeSnippetMapperProvider.class, method = "delCodeSnippetById")
  Integer delCodeSnippetById(boolean isAdmin, String username, String id);

  /**
   * get CodeSnippet by id
   *
   * @param id id
   * @return CodeSnippet
   */
  @Select("select * from code_snippet where enable_flag = 1 and id = #{id} ")
  @Results({
    @Result(id = true, column = "id", property = "id"),
    @Result(
        column = "FK_NOTE_BOOK_ID",
        property = "noteBook",
        one =
            @One(
                select = "cn.cnic.component.livy.mapper.NoteBookMapper.adminGetNoteBookById",
                fetchType = FetchType.LAZY))
  })
  CodeSnippet getCodeSnippetById(@Param("id") String id);

  /**
   * get CodeSnippet by id, Do not perform related queries
   *
   * @param id id
   * @return CodeSnippet
   */
  @Select("select * from code_snippet where enable_flag=1 and id=#{id} ")
  CodeSnippet getCodeSnippetByIdOnly(@Param("id") String id);

  /**
   * get CodeSnippetVo by id
   *
   * @param id id
   * @return CodeSnippetVo
   */
  @Select("select * from code_snippet where enable_flag=1 and id=#{id} ")
  CodeSnippetVo getCodeSnippetVoById(@Param("id") String id);

  /**
   * search CodeSnippet List
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   */
  @SelectProvider(type = CodeSnippetMapperProvider.class, method = "getCodeSnippetList")
  List<CodeSnippet> getCodeSnippetList(boolean isAdmin, String username, String param);

  /**
   * search CodeSnippetVo List
   *
   * @param isAdmin isAdmin
   * @param username username
   * @param param param
   */
  @SelectProvider(type = CodeSnippetMapperProvider.class, method = "getCodeSnippetList")
  List<CodeSnippetVo> getCodeSnippetVoList(boolean isAdmin, String username, String param);

  /**
   * search CodeSnippetVo List
   *
   * @param noteBookId noteBookId
   */
  @SelectProvider(type = CodeSnippetMapperProvider.class, method = "getCodeSnippetListByNoteBookId")
  List<CodeSnippet> getCodeSnippetListByNoteBookId(String noteBookId);
}
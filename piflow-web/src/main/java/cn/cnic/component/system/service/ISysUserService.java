package cn.cnic.component.system.service;

import cn.cnic.component.system.entity.SysUser;
import cn.cnic.component.system.vo.SysUserVo;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface ISysUserService {

  public SysUser findByUsername(String username);

  public String checkUserName(String username);

  public List<SysUser> findByName(String name);

  public List<SysUser> getUserList();

  public SysUser addUser(SysUser user);

  public int saveOrUpdate(SysUser user);

  public int deleteUser(String id);

  public String registerUser(SysUserVo sysUserVo);

  public String jwtLogin(String username, String password);

  void autoAddUser(String userName);
}

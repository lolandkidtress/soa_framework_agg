package com.James.Filter;

import com.James.basic.UtilsTools.Return;
import com.James.basic.Invoker.Invoker;


/**
 * Created by James on 2016/10/14.
 */
public interface Filter  {
  Return Filter(Invoker<?> invoker) throws Exception;
}

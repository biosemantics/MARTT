package visitor;
import java.lang.reflect.Method;
/**
 * <p>Title: VisitorAbstract</p>
 * <p>Description: an abstract class of Visitors, subclasses define three abstract
 *                 methods: visitElementComposite, visitElementLeaf, and visitObject</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Hong Cui
 * @version 1.0
 */

public abstract class VisitorAbstract implements ReflectiveVisitor {
  public VisitorAbstract() {
  }

  public abstract void visitElementComposite(ElementComposite ec, String alg);

  public abstract void visitElementLeaf(ElementLeaf el, String alg);

  public abstract void visitObject(Object o, String alg);

  public void dispatch(Object o, String alg){
    // Class.getName() returns package information as well.
    // This strips off the package information giving us
    // just the class name
    /*String methodName = o.getClass().getName();
    methodName = "visit"+
                 methodName.substring(methodName.lastIndexOf('.')+1);
    */
    // Now we try to invoke the method visit
    try {
       // Get the method visitFoo(Foo foo)
       Method m = getMethod(getClass(), o.getClass(),"".getClass());
       // Try to invoke visitFoo(Foo foo)
       m.invoke(this, new Object[] { o, alg });
    }/* catch (NoSuchMethodException e) {
       // No method, so do the default implementation
       visitDefault(o);
    }*/catch (IllegalAccessException e) {
    }catch (java.lang.reflect.InvocationTargetException e){

    }
  }

  protected Method getMethod(Class thisclass, Class c, Class string) {
   Class newc = c;
   Method m = null;
   // Try the superclasses
   while (m == null && newc != Object.class) {
      String method = newc.getName();
      method = "visit" + method.substring(method.lastIndexOf('.') + 1);
      try {
         m = getClass().getMethod(method, new Class[] {newc, string});
      } catch (NoSuchMethodException e) {
         newc = newc.getSuperclass();
      }
   }
   // Try the interfaces.  If necessary, you
   // can sort them first to define 'visitable' interface wins
   // in case an object implements more than one.
   if (newc == Object.class) {
      Class[] interfaces = c.getInterfaces();
      for (int i = 0; i < interfaces.length; i++) {
         String method = interfaces[i].getName();
         method = "visit" + method.substring(method.lastIndexOf('.') + 1);
         try {
            m = getClass().getMethod(method, new Class[] {interfaces[i], string});
         } catch (NoSuchMethodException e) {}
      }
   }
   if (m == null) {
      try {
         m = thisclass.getMethod("visitObject", new Class[] {Object.class, string});
      } catch (Exception e) {
          // Can't happen
      }
   }
   return m;
}


}
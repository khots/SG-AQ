package {
import mx.managers.SystemManager;
import flash.utils.*;
import flash.system.ApplicationDomain;
import mx.core.IFlexModuleFactory;
import mx.core.IFlexModule;
public class _DAG_mx_managers_SystemManager extends mx.managers.SystemManager implements IFlexModuleFactory {
  public function _DAG_mx_managers_SystemManager() {
      super();
}
override public function create(... params):Object
{
  if ((params.length == 0) || (params[0] is String))
  {
    var mainClassName:String = null;
    if (params.length == 0) mainClassName = 'DAG';
    else mainClassName = String(params[0]);
    var clazz:Class = Class(getDefinitionByName(mainClassName));
    if (clazz != null)
    {
      var inst:Object = new clazz();
      if (inst is IFlexModule)
      {
         (IFlexModule(inst)).moduleFactory = this;
      }
      return inst;
    }
    else return null;
  }
  else return super.create.apply(this, params);
}
  override public function info():Object { return {
"currentDomain": ApplicationDomain.currentDomain,
"creationComplete" : "initApp()",
"dragDrop" : "onDragDrop(event)",
"dragEnter" : "doDragEnter(event)",
"dragExit" : "onDragExit(event)",
"layout" : "vertical",
"mainClassName" : "DAG",
"mixins" : ["_DAG_FlexInit", "_alertButtonStyleStyle", "_ControlBarStyle", "_ScrollBarStyle", "_MenuStyle", "_activeTabStyleStyle", "_textAreaHScrollBarStyleStyle", "_ToolTipStyle", "_ComboBoxStyle", "_DragManagerStyle", "_DateFieldStyle", "_CheckBoxStyle", "_ListBaseStyle", "_DateChooserStyle", "_comboDropDownStyle", "_textAreaVScrollBarStyleStyle", "_ContainerStyle", "_globalStyle", "_windowStatusStyle", "_PanelStyle", "_MenuBarStyle", "_windowStylesStyle", "_activeButtonStyleStyle", "_ApplicationControlBarStyle", "_errorTipStyle", "_richTextEditorTextAreaStyleStyle", "_CursorManagerStyle", "_todayStyleStyle", "_dateFieldPopupStyle", "_TextInputStyle", "_plainStyle", "_dataGridStylesStyle", "_TitleWindowStyle", "_ApplicationStyle", "_headerDateTextStyle", "_ButtonStyle", "_DataGridStyle", "_CalendarLayoutStyle", "_AlertStyle", "_opaquePanelStyle", "_weekDayStyleStyle", "_headerDragProxyStyleStyle", "_DataGridItemRendererStyle", "_Components_CustomFormulaNodeWatcherSetupUtil", "_Components_SingleNodeCustomFormulaNodeWatcherSetupUtil", "_Components_JoinQueryNodeWatcherSetupUtil", "_Components_PopupWindowWatcherSetupUtil", "_Components_JoinQueryPopUpWindowWatcherSetupUtil", "_Components_DAGNodeWatcherSetupUtil", "_Components_singleNodeTQWidowWatcherSetupUtil", "_Components_AmbiguityPanelWatcherSetupUtil"]
,
"paddingBottom" : "0",
"paddingLeft" : "0",
"paddingRight" : "0",
"paddingTop" : "0",
"resize" : "onStageResize()"}; }
}}

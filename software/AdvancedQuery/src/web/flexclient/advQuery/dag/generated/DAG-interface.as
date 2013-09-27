package 
{
import flash.accessibility.*;
import flash.debugger.*;
import flash.display.*;
import flash.errors.*;
import flash.events.*;
import flash.external.*;
import flash.filters.*;
import flash.geom.*;
import flash.media.*;
import flash.net.*;
import flash.printing.*;
import flash.profiler.*;
import flash.system.*;
import flash.text.*;
import flash.ui.*;
import flash.utils.*;
import flash.xml.*;
import mx.binding.*;
import mx.containers.Canvas;
import mx.containers.VBox;
import mx.controls.Button;
import mx.controls.Label;
import mx.core.Application;
import mx.core.ClassFactory;
import mx.core.DeferredInstanceFromClass;
import mx.core.DeferredInstanceFromFunction;
import mx.core.IDeferredInstance;
import mx.core.IFactory;
import mx.core.IPropertyChangeNotifier;
import mx.core.mx_internal;
import mx.rpc.remoting.mxml.RemoteObject;
import mx.styles.*;
import mx.controls.Button;
import mx.containers.VBox;
import mx.containers.ApplicationControlBar;
import mx.controls.Label;
import mx.core.Application;

public class DAG extends mx.core.Application
{
	public function DAG() {}

	[Bindable]
	public var rpcService : mx.rpc.remoting.mxml.RemoteObject;
	[Bindable]
	public var mainPanelx : mx.containers.VBox;
	[Bindable]
	public var TQBtn : mx.controls.Button;
	[Bindable]
	public var JoinQueryBtn : mx.controls.Button;
	[Bindable]
	public var mainPanel : mx.containers.Canvas;
	[Bindable]
	public var currentExp : mx.controls.Label;

	public var _bindingsByDestination : Object;
	public var _bindingsBeginWithWord : Object;

include "E:/workplace/AdvancedQueryForJoin_NewCP/flexclient/advQuery/dag/DAG.mxml:49,2971";

}}

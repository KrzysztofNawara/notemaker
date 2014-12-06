%%%-------------------------------------------------------------------
%%% @author blueeyedhush
%%% @copyright (C) 2014, <COMPANY>
%%% @doc
%%%
%%% @end
%%% Created : 27. Nov 2014 9:32 AM
%%%-------------------------------------------------------------------
-module(guardianAngel).
-author("blueeyedhush").
-include("../include/global.hrl").

-import(messageEnCoder, [decode/1]).

%% API
-export([
  start/1
]).

start(ListenSocket) ->
  info_msg("New guardianAngel started"),
  {ok, AS} = gen_tcp:accept(ListenSocket),
  goodGod ! clientConnected,
  loop(AS).

loop(AS) ->
  receive
    {tcp, Socket, "{testquery}"} ->
      gen_tcp:send(Socket, "{testresponse}\n"),
      info_msg("Sent {testresponse}"),
      loop(AS);
    {tcp, Socket, Msg} ->
      ConvertedMesg = decode(Msg),
      dispatchMessage(ConvertedMesg, Socket),
      loop(AS);
    {tcp_closed, _} ->
      info_msg("Socket closed, so child is exiting"),
      exit(normal);
    A ->
      info_msg("Child received an unexpected present: ~p", [A]),
      loop(AS)
  end.


% called each time non-special message arrives over TCP
dispatchMessage(Socket, Msg) -> notImpl.
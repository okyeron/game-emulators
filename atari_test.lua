-- atari 2600 engine test
--
-- KEY 2 toggle sound on/off
-- KEY 3 trigger a sound
-- ENC 2 choose tone
-- ENC 3 change pitch
--

-- freq0, freq1 are 5 bit pitch (32 values 0-31)
-- vol0, vol1 are 4 bit volume control (16 values)
-- tone0, tone1 are as follows (16 values, 0-15):

-- Tone NAME    DESCRIPTION
--  0   nothing
--  1   Saw     sounds similar to a saw waveform
--  2			      low pitch
--  3   Engine  many 2600 games use this for an engine sound
-- 4/5  Square  a high pitched square waveform
--  6   Bass    fat bass sound
--  7   Pitfall log sound in pitfall, low and buzzy
--  8   Noise   white noise
--  9 			     similar to 7
-- 10   sine?
-- 11   nothing
-- 12/13   Lead    lower pitch square wave sound
-- 14   Bass    Another bassy sound
-- 15   Buzz    atonal buzz, good for percussion


engine.name = "Atari2600"

local MusicUtil = require "musicutil"

local midi_device
local mdevs = {}

local amp = 0.8
local tone0 = 12
local tone1 = 0
local freq0 = 30
local freq1 = 0
local vol0 = 0
local vol1 = 0
local pan = 0

local keystate = {false,false,false}

function init()

  engine.freq0(freq0)
  engine.tone0(tone0)
  engine.freq1(freq1)
  engine.tone1(tone1)
  engine.vol0(vol0)
  engine.vol1(vol1)
  engine.pan(pan)
  
  connect()
  get_midi_names()

   -- setup params
  
  params:add{type = "option", id = "midi_device", name = "MIDI-device", options = mdevs, default = 1,
    action = function(value)
      midi_device.event = nil
      midi_device = midi.connect(value)
      midi_device.event = midi_event
      midi.update_devices()

      mdevs = {}
      get_midi_names()
      params.params[1].options = mdevs
      --tab.print(params.params[1].options)
      devicepos = value
      print ("midi ".. devicepos .." selected: " .. mdevs[devicepos])
    end}

    redraw()
end

function toneHz(tone)
	hz = 30000 / tone / 10
	return util.round (hz, -2)   
end

function note_to_tone(note_num)
	freq = MusicUtil.note_num_to_freq (note_num)
--	print(freq)
	tone = 3000/freq
--	print(tone)
	return util.round(tone)
end

function get_midi_names()
  -- Get a list of midi devices
  for id,device in pairs(midi.vports) do
    mdevs[id] = device.name
  end
end

function connect()
  midi.update_devices()
  midi_device = midi.connect(devicepos)
  midi_device.event = midi_event
end

function noteOn(note_num, ch)
	freq = note_to_tone(note_num)
	if ch == 0 then
		freq0 = freq
	    engine.vol0(0) -- kill a previous held note?
		engine.freq0(freq)
		engine.vol0(15)
	elseif ch == 1 then 
		freq1 = freq
	    engine.vol1(0) -- kill a previous held note?
		engine.freq1(freq)
		engine.vol1(15)
	end
end
function noteOff(note_num, ch)
	if ch == 0 then
		engine.vol0(0)
	elseif ch == 1 then 
		engine.vol1(0)
	end
end

function midi_event(data)
  msg = midi.to_msg(data)
  if msg.type == "start" then
      clock.transport.reset()
      clock.transport.start()
  elseif msg.type == "continue" then
    if running then 
      clock.transport.stop()
    else 
      clock.transport.start()
    end
  end 
  if msg.type == "stop" then
    clock.transport.stop()
  end 

  if msg.type == "clock" then

  elseif data[1] == 0xfe then
    -- active sensing
    -- do nothing
    -- print("active sensing")
  else

    if msg.type and msg.type ~= "clock" then 
		if msg.type == "cc" then
			print(msg.cc)
		end
		if msg.type == 'note_on' then
			noteOn(msg.note, msg.ch-1)
--			noteOn(msg.note-12, msg.ch)
			redraw()
		end
		if msg.type == 'note_off' then
			noteOff(msg.note, msg.ch-1)
--			noteOff(msg.note, msg.ch)
		end

    end

    redraw()
  end
end


function key(n,z)
	if n == 1 and z == 1 then
		keystate[1]=true
	elseif n == 1 and z == 0 then
		keystate[1]=false
	end

	-- tone 0
	if n == 2 and z == 1 then
		engine.vol0(15)
	elseif n==2 and z ==0 then
		engine.vol0(0)
	end

	-- tone 1
	if n == 3 and z == 1 then
		engine.vol1(15)
	elseif n==3 and z ==0 then
		engine.vol1(0)
	end
  
end

function enc(n,d)
	if n == 1 then

	elseif n == 2 then
		if keystate[1] then
		  freq0 = util.clamp (freq0 + d, 0, 31)
		else
		  tone0 = util.clamp (tone0 + d, 0, 15)
		  print(tone0)
		end

	elseif n == 3 then
		if keystate[1] then
		  freq1 = util.clamp (freq1 + d, 0, 31)
		else
		  tone1 = util.clamp (tone1 + d, 0, 15)
		end

	end
	engine.tone0(tone0)
	engine.tone1(tone1)
	engine.freq0(freq0)
	engine.freq1(freq1)
	redraw()
end

function redraw()
  screen.clear()
  screen.level(15)
  screen.line_width(1)
  screen.font_face(0)
  screen.font_size(8)
  screen.move(1,8)
  screen.text( "2600" )
  screen.move(10,18)
  screen.text( "tone0: " .. tone0 )
  screen.move(10,28)
  screen.text( "freq0: " .. freq0 )
  screen.move(64,28)
  screen.text(toneHz(freq0).. "hz")

  screen.move(10,38)
  screen.text( "tone1: " .. tone1 )
  screen.move(10,48)
  screen.text( "freq1: " .. freq1 )
  screen.move(64,48)
  screen.text(toneHz(freq1).. "hz")

  screen.update()
end

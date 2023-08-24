-- Engine_SN76489 test
--

-- KEY 2 -- turn tone volumes to 15, release for vol 0
-- KEY 3 -- turn noise volume to 15, release for vol 0
-- ENC 1 choose tone0
-- ENC 2 choose tone1
-- ENC 3 change tone2
-- KEY 1 + ENC 1 choose noise

--
-- tone channels are 10bits 0-1023 -- lower values are higher pitches
-- volumes are 4bits 0-15
-- noise is 3bits 0-7

-- noise 0,1,2,3 are set tones, 4,5,6 are noise from high to low
-- noise 3 (periodic) and 7 (white noise) can be pitched with tone2
-- (set vol2 to zero in this situation)

-- noise chart
-- 0 Periodic noise, shift rate = clock speed (Hz) / 512
-- 1 Periodic noise, shift rate = clock speed (Hz) / 1024
-- 2 Periodic noise, shift rate = clock speed (Hz) / 2048
-- 3 Perioic noise, shift rate =  tone2 frequency
-- 4 White noise, shift rate = clock speed (Hz) / 512
-- 5 White noise, shift rate = clock speed (Hz) / 1024
-- 6 White noise, shift rate = clock speed (Hz) / 2048
-- 7 White noise, shift rate = tone2 frequency

engine.name = "SN76489"

local MusicUtil = require "musicutil"

local midi_device
local mdevs = {}

local amp = 0.8
local tone0 = math.random(1,1023)
local tone1 = math.random(1,1023)
local tone2 = math.random(1,1023)
local noise = 0
local vol0 = 0
local vol1 = 0
local vol2 = 0
local vol3 = 0
local keystate = {false,false,false}

local function round(number, digit_position) 
  local precision = math.pow(10, digit_position)
  number = number + (precision / 2)
  return math.floor(number / precision) * precision
end

function toneHz(tone)
	hz = 3579545 / (2 * tone * 16)
	return round (hz, -2)   
end

function note_to_tone(note_num)
	freq = MusicUtil.note_num_to_freq (note_num)
--	print(freq)
	tone = (3579545 / (32))/freq
	-- 111860.78125
	return util.round(tone)
end

function noteOn(note_num, ch)
	tone = note_to_tone(note_num)
	if ch == 0 then
		tone0 = tone
	    engine.vol0(0) -- kill a previous held note?
		engine.tone0(tone)
		engine.vol0(15)
	elseif ch == 1 then 
		tone1 = tone
	    engine.vol1(0) -- kill a previous held note?
		engine.tone1(tone)
		engine.vol1(15)
	elseif ch == 2 then 
		tone2 = tone
	    engine.vol2(0) -- kill a previous held note?
		engine.tone2(tone)
		engine.vol2(15)
	end
end
function noteOff(note_num, ch)
	if ch == 0 then
		engine.vol0(0)
	elseif ch == 1 then 
		engine.vol1(0)
	elseif ch == 2 then 
		engine.vol2(0)
	end
end


function init()

  engine.amp(amp)
  engine.tone0(tone0)
  engine.tone1(tone1)
  engine.tone2(tone2)
  engine.noise(noise)
  engine.vol0(vol0)
  engine.vol1(vol1)
  engine.vol2(vol2)
  engine.vol3(vol3)
 
  connect()
  get_midi_names()

   -- setup params
  
  params:add{type = "option", id = "midi_device", name = "MIDI-device", options = mdevs , default = 1,
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
      if clocking then 
        clock.cancel(blink_id)
        clocking = false
      end
      print ("midi ".. devicepos .." selected: " .. mdevs[devicepos])
      
    end}


  redraw()
end

function get_midi_names()
  -- Get a list of grid devices
  for id,device in pairs(midi.vports) do
    mdevs[id] = device.name
  end
end

function connect()
  midi.update_devices()
  midi_device = midi.connect(devicepos)
  midi_device.event = midi_event
end


function key(n,z)
  if n == 1 and z == 1 then
    keystate[1]=true
  elseif n == 1 and z == 0 then
    keystate[1]=false
  end

  -- tone channels
  if n == 2 and z == 1 then
    engine.vol0(15)
    engine.vol1(15)
    engine.vol2(15)
  elseif n==2 and z ==0 then
    engine.vol0(0)
    engine.vol1(0)
    engine.vol2(0)
  end
  -- noise
  if n == 3 and z == 1 then
    engine.vol3(15)
  elseif n==3 and z ==0 then
    engine.vol3(0)
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
    --print("active sensing")
  else
	if is_sysex_dump_on then
	  for _, b in pairs(data) do
		-- print(b)
		table.insert(sysex_payload, b)
		if b == 0xf7 then
		  is_sysex_dump_on = false
		  tab.print(sysex_payload)
		end
	  end
	elseif msg.type == 'sysex' then
	  is_sysex_dump_on = true
	  sysex_payload = {}
	  for _, b in pairs(msg.raw) do
		table.insert(sysex_payload, b)
	  end
	end

 
    if msg.type and msg.type ~= "clock" then 
		if msg.cc then
			print(msg.cc)
		end
		if msg.type == 'note_on' then
			noteOn(msg.note, msg.ch-1)
			noteOn(msg.note-12, msg.ch)
			redraw()
		end
		if msg.type == 'note_off' then
			noteOff(msg.note, msg.ch-1)
			noteOff(msg.note, msg.ch)
		end

    end

    redraw()
  end
end

function enc(n,d)
  if n == 1 then
    if keystate[1] then
      noise = util.clamp (noise + d, 0, 7)
    else
      tone0 = util.clamp (tone0 + d, 0, 1023)
    end
  
  elseif n == 2 then
    tone1 = util.clamp (tone1 + d, 0, 1023)
  elseif n == 3 then
    tone2 = util.clamp (tone2 + d, 0, 1023)
  end
  engine.tone0(tone0)
  engine.tone1(tone1)
  engine.tone2(tone2)
  engine.noise(noise)

  redraw()
end

function redraw()
  screen.clear()
  screen.level(15)
  screen.line_width(1)
  screen.font_face(0)
  screen.font_size(8)
  screen.move(1,8)
  screen.text( "SN76489" )
  screen.move(10,18)
  screen.text( "tone0: " .. tone0 )
  screen.move(64,18)
  screen.text(toneHz(tone0).. "hz")

  screen.move(10,28)
  screen.text( "tone1: " .. tone1)
  screen.move(64,28)
  screen.text(toneHz(tone1).. "hz")

  screen.move(10,38)
  screen.text( "tone2: " .. tone2)
  screen.move(64,38)
  screen.text(toneHz(tone2).. "hz")

  screen.move(10,48)
  screen.text( "noise: " .. noise )
  screen.update()
end


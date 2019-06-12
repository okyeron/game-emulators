// CroneEngine_Atari2600
// Atari2600

// two independent voices, each of which has a 4 bit volume control (16 values), 
// 5 bit pitch (32 values), and a 4 bit control register which selects the type of sound
// The standard labels for these registers are AUDV0 and AUDV1 for the volume registers, 
// AUDF0 and AUDF1 for the pitch registers, and AUDC0 and AUDC1 for the control registers.  
// The 5 bit pitch is very limited and the frequency values are simply divided down from 
// 30KHz reference frequency creating higher or lower pitch
// Note that setting the pitch register to a lower value results in a higher pitch.

// http://atarihq.com/danb/files/stella.pdf
// https://www.atariarchives.org/dev/tia/description.php

// args = 
// audc0, audc1, //(control registers - tone/sound selection)
// audf0, audf1, //(pitch registers)
// audv0, audv1, //(volume registers 0 = off, 15 = full)
// rate

//https://en.wikipedia.org/wiki/Television_Interface_Adaptor#Audio_Control_(AUDC0/1)
// tone codes
// 
// CODE NAME    DESCRIPTION
//  1   Saw     sounds similar to a saw waveform
//  2			low pitch
//  3   Engine  many 2600 games use this for an engine sound
//  4/5   Square  a high pitched square waveform
//  6   Bass    fat bass sound
//  7   Pitfall log sound in pitfall, low and buzzy
//  8   Noise   white noise
//  9 			similar to 7
// 10   sine?
// 12/13   Lead    lower pitch square wave sound
// 14	Bass	Another bassy sound
// 15   Buzz    atonal buzz, good for percussion
//

Engine_Atari2600 : CroneEngine {
	var pg;
    var amp=0.2;
    var attack=0.1;
    var sustain=0.5;
    var release=0.5;
    var tone0=5;	// see tone codes above
    var tone1=8;	// see tone codes above
  	var freq0=10;
    var freq1=20;
    var vol0=15;
    var vol1=15;

	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		pg = ParGroup.tail(context.xg);

		SynthDef("Atari2600", {
			arg out, tone0=tone0, tone1=tone1, freq0=freq0, freq1=freq1, amp=amp, attack=attack, sustain=sustain, release=release, pan=1 ;
			var e, z;
			//e = Env.perc(level: amp, releaseTime: release).kr(2);
			e = Env.linen(attackTime: attack, sustainTime: sustain, releaseTime: release, level: amp).kr(2);
			//e = EnvGen.kr(Env.asr(attack, sustain, release), gate, doneAction:2);
			z = Atari2600.ar(tone0, tone1, freq0, freq1, vol0, vol1);
			Out.ar(out, Pan2.ar(z*e, pan));
		}).add;


		this.addCommand("trig", "", {
        	Synth("Atari2600", [\out, context.out_b, \freq0,freq0,\freq1,freq1,\tone0,tone0,\tone1,tone1,\vol0,vol0,\vol1,vol1,\amp,amp,\release,release,\attack,attack,\sustain,sustain], target:pg);
		});

		this.addCommand("freq0", "f", { arg msg;
			freq0 = msg[1];
		});
		this.addCommand("freq1", "f", { arg msg;
			freq1 = msg[1];
		});
		this.addCommand("tone0", "f", { arg msg;
			tone0 = msg[1];
		});
		this.addCommand("tone1", "f", { arg msg;
			tone1 = msg[1];
		});
		this.addCommand("vol0", "f", { arg msg;
			vol0 = msg[1];
		});
		this.addCommand("vol1", "f", { arg msg;
			vol1 = msg[1];
		});
		this.addCommand("amp", "f", { arg msg;
			amp = msg[1];
		});
		this.addCommand("attack", "f", { arg msg;
			attack = msg[1];
		});
		this.addCommand("sustain", "f", { arg msg;
			sustain = msg[1];
		});
		this.addCommand("release", "f", { arg msg;
			release = msg[1];
		});

	}
}

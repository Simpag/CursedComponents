<p align="center">
    <img src="img/icon.png" width="100" height="100">
    <h1 align="center">CursedComponents</h1>

## About the Mod

**CursedComponents** is a Minecraft mod that introduces new blocks and items, many of which are inspired by elements
from mods that worked with past versions of Minecraft.

> **Note:** CursedComponents is currently in active development. The mod is not yet ready for a full release, so expect
> updates and potential changes.

## Blocks

### Dimensional Quarry

<p align="center">
    <img src="img/dimensional_quarry.png" width="500">

Dimensional Quarry is a machine that mine blocks in a parallel dimension, replacing the world eater ruining your world.
The parallel dimension is an exact replica of the corresponding dimensions, thus mod compatability is assured. The
Dimensional Quarry requires a Netherite Pickaxe and a Mini Chunk in order to operate. The choice
of [Mini Chunk](#mini-chunk)
determines which dimension the Dimensional Quarry mines from. In its base form it uses 40'000 FE/t mining one block
every 10 ticks. Upgrading the Dimensional Quarry is as easy as enchanting the Netherite Pickaxe with efficiency,
unbreaking and/or fortune/silk touch. Efficiency V increases the mining speed up to one block per tick but increases the
energy consumption by 250%. Unbreaking reduces the energy consumption, down to 50%, while fortune and silk touch work
like normal. Silk touch and fortune III increases the energy consumption by 150%. A max upgraded Dimensional Quarry
consumes 75'000 FE/t. In case you do not want your Dimensional Quarry to mine a certain block, add
a [Dimensional Quarry Item Filter](#dimensional-quarry-item-filter).

The Dimensional Quarry is lag friendly, occasional lag spikes (once ~20 minutes at full speed) due to chunk generation.

<p align="center">
    <img src="img/dimensional_quarry_lag.png" width="500">
    <h4 align="center">64 Dimensional Quarries running at full speed using fortune III</h4>

### Mini Chunk

Three Mini Chunk blocks are added; one for the overworld, the nether and the end. These are used to
select which dimension the [Dimensional Quarry](#dimensional-quarry) operates in, or simply as decoration pieces.

### Lucky Block
<p align="center">
    <img src="img/lucky_block.png" width="500">

Inspired by previous Lucky Block mods, the Cursed Components Lucky Block functions similarly to the classics, with a few
twists. New animations, structure generation and loot have been added in order to elevate and incorporate the lucky
block into your world. Unique structures can be found in the world, containing Lucky Blocks, with the ability to reward
or punish whoever dare to open them.

### Conveyor Belt
<p align="center">
    <img src="img/conveyor_belts.png" width="500">

CursedComponents offers two tiers of conveyor belts, each capable of transporting items and entities at different speeds. Additionally, grass grows beneath conveyor belts, 
making them ideal for automated farming setups that require grass blocks, such as sheep farms.

### Auto Shearer
<p align="center">
    <img src="img/auto_shearer.png" width="500">

The Auto Shearer is a non-collidable block that automatically shears entities that come into contact with it. It is particularly useful for automating sheep shearing or handling other shearable entities. Grass also grows beneath the Auto Shearer, 
allowing for convenient and efficient integration into farming systems.

## Items

### Dimensional Quarry Item Filter

A simple item used in the [Dimensional Quarry](#dimensional-quarry) in order to blacklist certain items to be mined. It
supports up to 5 blacklisted items. The [Dimensional Quarry](#dimensional-quarry) will still mine at the same speed as
if the blacklist was not present but the blocks will not be added to the
internal [Dimensional Quarry](#dimensional-quarry) inventory.

### Sponge On a Stick

Sponge On a Stick is an item which works just like the normal sponge, except that it's attached to a stick! The Sponge
On a Stick runs on energy (FE) and consumes 500 FE per use with a total capacity of 10'000 FE.

### Crafting Ingredients

* ``Warden Infusion`` New drop from killing the Warden with a drop rate of 1-3
* ``Warden Infused Diamond``
* ``Dimensional Core``

## Supported Versions

CursedComponents is currently being developed for Minecraft 1.21.x. Support for other versions may be added in the
future.

## License

This mod's source code is licensed under the [GNU General Public License v3.0](../LICENSE), with the exception of
bundled APIs from other mods, which retain their respective licenses.

<?php

namespace Tests\Unit;

use App\Support\RfidGenerator;
use Tests\TestCase;

class RfidGeneratorTest extends TestCase
{
    #[\PHPUnit\Framework\Attributes\Test]
    public function it_generates_valid_cattle_tags()
    {
        $tag = RfidGenerator::generateCattleTag();
        $this->assertTrue(RfidGenerator::isValid($tag));
        $this->assertStringStartsWith('C', $tag);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function it_generates_valid_vet_tags()
    {
        $tag = RfidGenerator::generateVetTag();
        $this->assertTrue(RfidGenerator::isValid($tag));
        $this->assertStringStartsWith('V', $tag);
    }

    #[\PHPUnit\Framework\Attributes\Test]
    public function it_validates_various_tags()
    {
        // Valid tags
        $this->assertTrue(RfidGenerator::isValid('C12345'));
        $this->assertTrue(RfidGenerator::isValid('VABC12345678901')); // 16 chars

        // Specialized checks
        $this->assertTrue(RfidGenerator::isCattleTag('C12345'));
        $this->assertFalse(RfidGenerator::isCattleTag('V12345'));
        $this->assertTrue(RfidGenerator::isVetTag('V12345'));
        $this->assertFalse(RfidGenerator::isVetTag('C12345'));

        // Invalid tags
        $this->assertFalse(RfidGenerator::isValid(''));
        $this->assertFalse(RfidGenerator::isValid(null));
        $this->assertFalse(RfidGenerator::isValid('A123'));
        $this->assertFalse(RfidGenerator::isValid('C')); // Too short
        $this->assertFalse(RfidGenerator::isValid('C12345678901234567')); // Too long (17 chars)
        $this->assertFalse(RfidGenerator::isValid('C123-456'));
    }
}
